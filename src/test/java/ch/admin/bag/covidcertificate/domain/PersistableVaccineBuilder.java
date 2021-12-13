package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.api.request.Issuable;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Slf4j
public class PersistableVaccineBuilder {

    public final static LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1);
    public final static LocalDateTime TOMORROW = LocalDateTime.now().plusDays(1);

    private String code = "EU/1/20/1528";
    private String display = "Comirnaty";
    private boolean active = true;
    private boolean chIssuable = true;

    private String prophylaxisCode = "1119349007";
    private String prophylaxisDisplayName = "SARS-CoV-2 mRNA vaccine";
    private boolean prophylaxisActive = true;

    private String authHolderCode = "ORG-100030215";
    private String authHolderDisplayName = "Biontech Manufacturing GmbH";
    private boolean authHolderActive = true;

    private int vaccineOrder = 200;
    private boolean webUiSelectable = false;
    private boolean apiGatewaySelectable = false;
    private boolean apiPlatformSelectable = false;
    private boolean swissMedic = false;
    private boolean emea = false;
    private boolean whoEul = false;
    private String analogVaccine = null;
    private LocalDateTime validFrom = YESTERDAY;
    private LocalDateTime validTo = TOMORROW;


    private PersistableVaccineBuilder() {
    }

    /**
     * Defaults:
     * <ul>
     *     <li>code="EU/1/20/1528", display="Comirnaty", active=true, chIssuable=true</li>
     *     <li>prophylaxisCode="1119349007" <br>prophylaxisDisplayName="SARS-CoV-2 mRNA vaccine" <br>prophylaxisActive=true</li>
     *     <li>authHolderCode="ORG-100030215" <br>authHolderDisplayName="Biontech Manufacturing GmbH" <br>authHolderActive=true</li>
     *     <li>vaccineOrder=200 <br>webUiSelectable=false <br>apiGatewaySelectable=false <br>apiPlatformSelectable=false</li>
     *     <li>swissMedic=false, emea=false, whoEul=false, analogVaccine=null</li>
     *     <li>validFrom=YESTERDAY, validTo = TOMORROW</li>
     *
     *
     * </ul>
     * @return builder
     */
    static public PersistableVaccineBuilder usingDefaults() {
        return new PersistableVaccineBuilder();
    }

    public PersistableVaccineBuilder code(String code) {
        this.code = code;
        return this;
    }

    public PersistableVaccineBuilder active(boolean active) {
        return this.active(active, "Test " + (active ? "" : "not ") + "active");
    }

    public PersistableVaccineBuilder active(boolean active, String displayName) {
        this.active = active;
        this.display = displayName;
        return this;
    }

    public PersistableVaccineBuilder chIssuable(boolean chIssuable) {
        this.chIssuable = chIssuable;
        return this;
    }

    public PersistableVaccineBuilder prophylaxisActive(boolean active) {
        return this.prophylaxisActive(active, "Prophylaxis " + (active ? "" : "not ") + "active");
    }

    public PersistableVaccineBuilder prophylaxisActive(boolean active, String displayName) {
        this.prophylaxisActive = active;
        this.prophylaxisDisplayName = displayName;
        return this;
    }

    public PersistableVaccineBuilder authHolderActive(boolean active) {
        return this.authHolderActive(active, "Test company " + (active ? "" : "not ") + "active");
    }

    public PersistableVaccineBuilder authHolderActive(boolean active, String displayName) {
        this.authHolderActive = active;
        this.authHolderDisplayName = displayName;
        return this;
    }

    public PersistableVaccineBuilder order(int orderNumber) {
        this.vaccineOrder = orderNumber;
        return this;
    }

    public PersistableVaccineBuilder webUI(boolean selectable) {
        this.webUiSelectable = selectable;
        return this;
    }

    public PersistableVaccineBuilder apiGateway(boolean selectable) {
        this.apiGatewaySelectable = selectable;
        return this;
    }

    public PersistableVaccineBuilder apiPlatform(boolean selectable) {
        this.apiPlatformSelectable = selectable;
        return this;
    }

    public PersistableVaccineBuilder validFromTo(LocalDateTime from, LocalDateTime to) {
        this.validFrom = from;
        this.validTo = to;
        return this;
    }

    public void persist(EntityManager entityManager) {
        log.debug("persisting [code={},display={},active={},chIssuable={}," +
                        "prophylaxisCode={},prophylaxisDisplayName={},prophylaxisActive={}," +
                        "authHolderCode={},authHolderDisplayName={},authHolderActive={}," +
                        "vaccineOrder={}," +
                        "webUiSelectable={},apiGatewaySelectable={},apiPlatformSelectable={}," +
                        "swissMedic={},emea={},whoEul={}," +
                        "analogVaccine={}," +
                        "validFrom={},validTo={}]",
                code, display, active, chIssuable,
                prophylaxisCode, prophylaxisDisplayName, prophylaxisActive,
                authHolderCode, authHolderDisplayName, authHolderActive,
                vaccineOrder,
                webUiSelectable, apiGatewaySelectable, apiPlatformSelectable,
                swissMedic, emea, whoEul,
                analogVaccine,
                validFrom, validTo);

        Vaccine vaccine = new Vaccine(
                code,
                display,
                active,
                chIssuable,
                Issuable.CH_ONLY,
                vaccineOrder,
                webUiSelectable,
                apiGatewaySelectable,
                apiPlatformSelectable,
                swissMedic,
                emea,
                whoEul,
                analogVaccine,
                validFrom,
                validTo
        );
        Prophylaxis prophylaxis = new Prophylaxis(
                prophylaxisCode,
                prophylaxisDisplayName,
                prophylaxisActive);
        vaccine.setProphylaxis(prophylaxis);
        entityManager.persist(prophylaxis);
        AuthHolder authHolder = new AuthHolder(
                authHolderCode,
                authHolderDisplayName,
                authHolderActive);
        vaccine.setAuthHolder(authHolder);
        entityManager.persist(authHolder);
        entityManager.persist(vaccine);
    }
}
