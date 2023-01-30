package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;

import java.util.List;

public class SigningInformationMapper {

    private SigningInformationMapper() {
        throw new IllegalStateException("SigningInformationMapper is a utility class");
    }

    public static SigningInformationDto fromEntity(SigningInformation signingInformation) {
        return SigningInformationDto.builder()
                .certificateType(signingInformation.getCertificateType())
                .code(signingInformation.getCode())
                .alias(signingInformation.getAlias())
                .certificateAlias(signingInformation.getCertificateAlias())
                .slotNumber(signingInformation.getSlotNumber())
                .validFrom(signingInformation.getValidFrom())
                .validTo(signingInformation.getValidTo())
                .build();
    }

    public static List<SigningInformationDto> fromEntityList(List<SigningInformation> signingInformationList) {
        return signingInformationList.stream().map(SigningInformationMapper::fromEntity).toList();
    }
}
