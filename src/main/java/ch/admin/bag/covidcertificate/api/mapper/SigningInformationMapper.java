package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;

import java.util.List;
import java.util.stream.Collectors;

public class SigningInformationMapper {

    public static SigningInformationDto fromEntity(SigningInformation signingInformation) {
        return new SigningInformationDto.SigningInformationDtoBuilder()
                .withCertificateType(signingInformation.getCertificateType())
                .withCode(signingInformation.getCode())
                .withAlias(signingInformation.getAlias())
                .withCertificateAlias(signingInformation.getCertificateAlias())
                .withValidFrom(signingInformation.getValidFrom())
                .withValidTo(signingInformation.getValidTo())
                .build();
    }

    public static List<SigningInformationDto> fromEntityList(List<SigningInformation> signingInformationList) {
        return signingInformationList.stream().map(one -> fromEntity(one)).collect(Collectors.toList());
    }
}
