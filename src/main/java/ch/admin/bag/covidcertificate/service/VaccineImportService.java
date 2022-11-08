package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.Issuable;
import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.vaccines.VaccineValueSetsClient;
import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.AuthHolderRepository;
import ch.admin.bag.covidcertificate.domain.DisplayNameModification;
import ch.admin.bag.covidcertificate.domain.DisplayNameModificationRepository;
import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.ProphylaxisRepository;
import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import ch.admin.bag.covidcertificate.domain.VaccineImportControlRepository;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import ch.admin.bag.covidcertificate.domain.ValueSetUpdateLog;
import ch.admin.bag.covidcertificate.domain.ValueSetUpdateLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccineImportService {

    private final VaccineValueSetsClient valueSetsClient;

    private final ValueSetUpdateLogRepository valueSetUpdateLogRepository;

    private final VaccineImportControlRepository vaccineImportControlRepository;

    private final DisplayNameModificationRepository displayNameModificationRepository;

    private final VaccineRepository vaccineRepository;

    private final AuthHolderRepository authHolderRepository;

    private final ProphylaxisRepository prophylaxisRepository;

    @Transactional
    public boolean updateValueSetOfVaccines(LocalDate importDate) {
        Optional<VaccineImportControl> importControlOptional =
                this.vaccineImportControlRepository.findByImportDate(importDate);
        if (importControlOptional.isPresent()) {
            VaccineImportControl vaccineImportControl = importControlOptional.get();
            log.info("Identified value set update version {} for date {} to be processed",
                    vaccineImportControl.getImportVersion(),
                    vaccineImportControl.getImportDate());
            boolean isProcessingDone = this.processValueSetUpdate(vaccineImportControl);
            vaccineImportControl.setDone(isProcessingDone);
            this.vaccineImportControlRepository.save(vaccineImportControl);
            return true;
        } else {
            return false;
        }
    }

    private boolean processValueSetUpdate(VaccineImportControl vaccineImportControl) {
        Map<String, VaccineValueSetDto> vaccineResult = this.valueSetsClient.getVaccineValueSet(vaccineImportControl);
        boolean vaccinesDone = this.processVaccines(vaccineResult);
        Map<String, AuthHolderValueSetDto> authHolderResult = this.valueSetsClient.getAuthHolderValueSet(vaccineImportControl);
        boolean authHoldersDone = this.processAuthHolders(authHolderResult);
        Map<String, ProphylaxisValueSetDto> prophylaxisResult = this.valueSetsClient.getProphylaxisValueSet(vaccineImportControl);
        boolean prophylaxisDone = this.processProphylaxis(prophylaxisResult);
        return vaccinesDone && authHoldersDone && prophylaxisDone;
    }

    private boolean processVaccines(Map<String, VaccineValueSetDto> vaccineResult) {
        try {
            List<String> managedVaccineCodes = this.vaccineRepository.findAllCodes();
            vaccineResult.entrySet().stream().forEach(entry -> this.processOneVaccine(entry, managedVaccineCodes));
            // after processing managedVaccineCodes contains only those who got deleted from the value set.
            // this does not mean that we delete them directly. But we write a change log.
            for (String codeToDelete : managedVaccineCodes) {
                this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                        .entityType(EntityType.VACCINE)
                        .code(codeToDelete)
                        .updateAction(UpdateAction.DELETE)
                        .updatedAt(LocalDateTime.now())
                        .build());
            }
            return true;
        } catch (Exception ex) {
            log.error("Problem processing vaccine value set", ex);
            return false;
        }
    }

    private void processOneVaccine(Map.Entry<String, VaccineValueSetDto> entry, List<String> managedVaccineCodes) {
        final String code = entry.getKey();
        final VaccineValueSetDto value = entry.getValue();
        final LocalDateTime now = LocalDateTime.now();
        List<Vaccine> foundVaccines = this.vaccineRepository.findByCode(code);
        Optional<DisplayNameModification> modifiedDisplayName = this.displayNameModificationRepository
                .findByCodeAndEntityType(code, EntityType.VACCINE);
        final String displayName = modifiedDisplayName.isPresent() ? modifiedDisplayName.get().getDisplay() : value.getDisplay();
        if (CollectionUtils.isEmpty(foundVaccines)) {
            // new Vaccine in value set
            Vaccine newVaccine = Vaccine.builder()
                    .code(code)
                    .display(displayName)
                    .active(value.isActive())
                    .createdAt(now)
                    .modifiedAt(now)
                    .issuable(Issuable.UNDEFINED)
                    .vaccineOrder(500) // sort to the bottom
                    .build();
            this.vaccineRepository.save(newVaccine);
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.VACCINE)
                    .code(code)
                    .updateAction(UpdateAction.NEW)
                    .updatedAt(now)
                    .build());
        } else {
            // update all found Vaccines using the same values. There could be duplicates of one code with different
            // control flags
            for (Vaccine toBeUpdated : foundVaccines) {
                toBeUpdated.setDisplay(displayName);
                toBeUpdated.setActive(value.isActive());
                toBeUpdated.setModifiedAt(now);
                this.vaccineRepository.save(toBeUpdated);
            }
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.VACCINE)
                    .code(code)
                    .updateAction(UpdateAction.UPDATE)
                    .updatedAt(now)
                    .build());
        }
        managedVaccineCodes.remove(code);
    }

    private boolean processAuthHolders(Map<String, AuthHolderValueSetDto> authHolderResult) {
        try {
            List<String> managedAuthHolderCodes = this.authHolderRepository.findAllCodes();
            authHolderResult.entrySet().stream().forEach(entry -> this.processOneAuthHolder(entry, managedAuthHolderCodes));
            // after processing managedAuthHolderCodes contains only those who got deleted from the value set.
            // this does not mean that we delete them directly. But we write a change log.
            for (String codeToDelete : managedAuthHolderCodes) {
                this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                        .entityType(EntityType.AUTH_HOLDER)
                        .code(codeToDelete)
                        .updateAction(UpdateAction.DELETE)
                        .updatedAt(LocalDateTime.now())
                        .build());
            }
            return true;
        } catch (Exception ex) {
            log.error("Problem processing auth holder value set", ex);
            return false;
        }
    }

    private void processOneAuthHolder(Map.Entry<String, AuthHolderValueSetDto> entry, List<String> managedAuthHolderCodes) {
        final String code = entry.getKey();
        final AuthHolderValueSetDto value = entry.getValue();
        final LocalDateTime now = LocalDateTime.now();
        AuthHolder foundAuthHolder = this.authHolderRepository.findByCode(code);
        Optional<DisplayNameModification> modifiedDisplayName = this.displayNameModificationRepository
                .findByCodeAndEntityType(code, EntityType.AUTH_HOLDER);
        final String displayName = modifiedDisplayName.isPresent() ? modifiedDisplayName.get().getDisplay() : value.getDisplay();
        if (foundAuthHolder == null) {
            // new AuthHolder in value set
            AuthHolder authHolder = AuthHolder.builder()
                    .code(code)
                    .display(displayName)
                    .active(value.isActive())
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();
            this.authHolderRepository.save(authHolder);
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.AUTH_HOLDER)
                    .code(code)
                    .updateAction(UpdateAction.NEW)
                    .updatedAt(now)
                    .build());
        } else {
            // update found AuthHolder using the same values
            foundAuthHolder.setDisplay(displayName);
            foundAuthHolder.setActive(value.isActive());
            foundAuthHolder.setModifiedAt(now);
            this.authHolderRepository.save(foundAuthHolder);
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.AUTH_HOLDER)
                    .code(code)
                    .updateAction(UpdateAction.UPDATE)
                    .updatedAt(now)
                    .build());
        }
        managedAuthHolderCodes.remove(code);
    }

    private boolean processProphylaxis(Map<String, ProphylaxisValueSetDto> prophylaxisResult) {
        try {
            List<String> managedProphylaxisCodes = this.prophylaxisRepository.findAllCodes();
            prophylaxisResult.entrySet().stream().forEach(entry -> this.processOneProphylaxis(entry, managedProphylaxisCodes));
            // after processing managedProphylaxisCodes contains only those who got deleted from the value set.
            // this does not mean that we delete them directly. But we write a change log.
            for (String codeToDelete : managedProphylaxisCodes) {
                this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                        .entityType(EntityType.PROPHYLAXIS)
                        .code(codeToDelete)
                        .updateAction(UpdateAction.DELETE)
                        .updatedAt(LocalDateTime.now())
                        .build());
            }
            return true;
        } catch (Exception ex) {
            log.error("Problem processing prophylaxis value set", ex);
            return false;
        }
    }

    private void processOneProphylaxis(Map.Entry<String, ProphylaxisValueSetDto> entry, List<String> managedProphylaxisCodes) {
        final String code = entry.getKey();
        final ProphylaxisValueSetDto value = entry.getValue();
        final LocalDateTime now = LocalDateTime.now();
        Prophylaxis foundProphylaxis = this.prophylaxisRepository.findByCode(code);
        Optional<DisplayNameModification> modifiedDisplayName = this.displayNameModificationRepository
                .findByCodeAndEntityType(code, EntityType.PROPHYLAXIS);
        final String displayName = modifiedDisplayName.isPresent() ? modifiedDisplayName.get().getDisplay() : value.getDisplay();
        if (foundProphylaxis == null) {
            // new Prophylaxis in value set
            Prophylaxis prophylaxis = Prophylaxis.builder()
                    .code(code)
                    .display(displayName)
                    .active(value.isActive())
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();
            this.prophylaxisRepository.save(prophylaxis);
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.PROPHYLAXIS)
                    .code(code)
                    .updateAction(UpdateAction.NEW)
                    .updatedAt(now)
                    .build());
        } else {
            // update found Prophylaxis using the same values
            foundProphylaxis.setDisplay(displayName);
            foundProphylaxis.setActive(value.isActive());
            foundProphylaxis.setModifiedAt(now);
            this.prophylaxisRepository.save(foundProphylaxis);
            this.valueSetUpdateLogRepository.save(ValueSetUpdateLog.builder()
                    .entityType(EntityType.PROPHYLAXIS)
                    .code(code)
                    .updateAction(UpdateAction.UPDATE)
                    .updatedAt(now)
                    .build());
        }
        managedProphylaxisCodes.remove(code);
    }
}
