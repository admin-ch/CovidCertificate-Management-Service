package ch.admin.bag.covidcertificate.testutil.pdf.locator;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public final class PdfCertificatesRegionsLocators {

    public static final class VaccinationCertificatesRegionsLocators {
        public static final String documentTitlePrimaryLang = "documentTitlePrimaryLang";
        public static final String documentTitleSecondaryLang = "documentTitleSecondaryLang";
        public static final String certificateTitlePrimaryLang = "certificateTitlePrimaryLang";
        public static final String certificateTitleSecondaryLang = "certificateTitleSecondaryLang";
        public static final String diseaseLabelPrimaryLang = "diseaseLabelPrimaryLang";
        public static final String diseaseLabelSecondaryLang = "diseaseLabelSecondaryLang";
        public static final String diseaseValue = "diseaseValue";
        public static final String dosisLabelPrimaryLang = "dosisLabelPrimaryLang";
        public static final String dosisLabelSecondaryLang = "dosisLabelSecondaryLang";
        public static final String dosisValue = "dosisValue";
        public static final String vaccineTypeLabelPrimaryLang = "vaccineTypeLabelPrimaryLang";
        public static final String vaccineTypeLabelSecondaryLang = "vaccineTypeLabelSecondaryLang";
        public static final String vaccineTypeValue = "vaccineTypeValue";
        public static final String productLabelPrimaryLang = "productLabelPrimaryLang";
        public static final String productLabelSecondaryLang = "productLabelSecondaryLang";
        public static final String productValue = "productValue";
        public static final String manufacturerLabelPrimaryLang = "manufacturerLabelPrimaryLang";
        public static final String manufacturerLabelSecondaryLang = "manufacturerLabelSecondaryLang";
        public static final String manufacturerValue = "manufacturerValue";
        public static final String vaccinationDateLablePrimaryLang = "vaccinationDateLablePrimaryLang";
        public static final String vaccinationDateLabelSecondaryLang = "vaccinationDateLabelSecondaryLang";
        public static final String vaccinationDateValue = "vaccinationDateValue";
        public static final String countryOfVaccinationLabelPrimaryLang = "countryOfVaccinationLabelPrimaryLang";
        public static final String countryOfVaccinationLabelSecondaryLang = "countryOfVaccinationLabelSecondaryLang";
        public static final String countryOfVaccinationValuePrimaryLang = "countryOfVaccinationValuePrimaryLang";
        public static final String countryOfVaccinationValueSecondaryLang = "countryOfVaccinationValueSecondaryLang";
        public static final String uvci = "uvci";
        public static final String creationDateTextPrimaryLang = "creationDateTextPrimaryLang";
        public static final String creationDateTextSecondaryLang = "creationDateTextSecondaryLang";
        public static final String usedDateFormat = "usedDateFormat";
        public static final String personalInfoTitlePrimaryLang = "personalInfoTitlePrimaryLang";
        public static final String personalInfoTitleTitleSecondaryLang = "personalInfoTitleTitleSecondaryLang";
        public static final String nameLabelPrimaryLang = "nameLabelPrimaryLang";
        public static final String nameLabelSecondaryLang = "nameLabelSecondaryLang";
        public static final String nameValue = "nameValue";
        public static final String birthDateLabelPrimaryLang = "birthDateLabelPrimaryLang";
        public static final String birthDateLabelSecondaryLang = "birthDateLabelSecondaryLang";
        public static final String birthDateValue = "birthDateValue";
        public static final String issuerLabelPrimaryLang = "issuerLabelPrimaryLang";
        public static final String issuerLabelSecondaryLang = "issuerLabelSecondaryLang";
        public static final String issuerValuePrimaryLang = "issuerValuePrimaryLang";
        public static final String issuerValueSecondaryLang = "issuerValueSecondaryLang";
        public static final String disclaimerValuePrimaryLang = "disclaimerValuePrimaryLang";
        public static final String disclaimerValueSecondaryLang = "disclaimerValueSecondaryLang";
        public static final String useAppValuePrimaryLang = "useAppValuePrimaryLang";
        public static final String useAppValueSecondaryLang = "useAppValueSecondaryLang";
        public static final String infoLineValue = "infoLineValue";
        public static final String appName = "appName";


        private static final Map<String, PdfTextLocator.LocalisationData> regionLocatorsMap = Map.ofEntries(
                entry(documentTitlePrimaryLang, new PdfTextLocator.LocalisationData(documentTitlePrimaryLang, 292.587494f, 39.773155f, 570f, 57.699974f)),
                entry(documentTitleSecondaryLang, new PdfTextLocator.LocalisationData(documentTitleSecondaryLang, 292.587494f, 65.363602f, 570f, 76.149986f)),
                entry(certificateTitlePrimaryLang, new PdfTextLocator.LocalisationData(certificateTitlePrimaryLang, 293.337494f, 103.920349f, 570f, 111.287498f)),
                entry(certificateTitleSecondaryLang, new PdfTextLocator.LocalisationData(certificateTitleSecondaryLang, 293.337494f, 115.637306f, 570f, 121.787498f)),
                entry(diseaseLabelPrimaryLang, new PdfTextLocator.LocalisationData(diseaseLabelPrimaryLang, 293.337494f, 140.520187f, 422f, 147.550018f)),
                entry(diseaseLabelSecondaryLang, new PdfTextLocator.LocalisationData(diseaseLabelSecondaryLang, 293.337494f, 151.712311f, 422f, 157.862518f)),
                entry(diseaseValue, new PdfTextLocator.LocalisationData(diseaseValue, 427.062500f, 140.257675f, 570f, 147.287506f)),
                entry(dosisLabelPrimaryLang, new PdfTextLocator.LocalisationData(dosisLabelPrimaryLang, 293.337494f, 173.595200f, 422f, 180.625031f)),
                entry(dosisLabelSecondaryLang, new PdfTextLocator.LocalisationData(dosisLabelSecondaryLang, 293.337494f, 184.787323f, 422f, 190.937531f)),
                entry(dosisValue, new PdfTextLocator.LocalisationData(dosisValue, 427.812500f, 175.614471f, 570f, 184.000031f)),
                entry(vaccineTypeLabelPrimaryLang, new PdfTextLocator.LocalisationData(vaccineTypeLabelPrimaryLang, 293.337494f, 206.670151f, 422f, 213.699982f)),
                entry(vaccineTypeLabelSecondaryLang, new PdfTextLocator.LocalisationData(vaccineTypeLabelSecondaryLang, 293.337494f, 217.862274f, 422f, 224.012482f)),
                entry(vaccineTypeValue, new PdfTextLocator.LocalisationData(vaccineTypeValue, 427.062500f, 206.407700f, 570f, 213.437531f)),
                entry(productLabelPrimaryLang, new PdfTextLocator.LocalisationData(productLabelPrimaryLang, 293.337494f, 239.745163f, 422f, 246.774994f)),
                entry(productLabelSecondaryLang, new PdfTextLocator.LocalisationData(productLabelSecondaryLang, 293.337494f, 250.937286f, 422f, 257.087494f)),
                entry(productValue, new PdfTextLocator.LocalisationData(productValue, 427.062500f, 239.482651f, 570f, 246.512482f)),
                entry(manufacturerLabelPrimaryLang, new PdfTextLocator.LocalisationData(manufacturerLabelPrimaryLang, 293.337494f, 272.820190f, 422f, 279.850006f)),
                entry(manufacturerLabelSecondaryLang, new PdfTextLocator.LocalisationData(manufacturerLabelSecondaryLang, 293.337494f, 284.012299f, 422f, 290.162506f)),
                entry(manufacturerValue, new PdfTextLocator.LocalisationData(manufacturerValue, 427.062500f, 272.557678f, 570f, 291.587494f)),
                entry(vaccinationDateLablePrimaryLang, new PdfTextLocator.LocalisationData(vaccinationDateLablePrimaryLang, 293.337494f, 307.320190f, 422f, 314.350006f)),
                entry(vaccinationDateLabelSecondaryLang, new PdfTextLocator.LocalisationData(vaccinationDateLabelSecondaryLang, 293.337494f, 318.512299f, 422f, 324.662506f)),
                entry(vaccinationDateValue, new PdfTextLocator.LocalisationData(vaccinationDateValue, 427.062500f, 307.057678f, 570f, 314.087494f)),
                entry(countryOfVaccinationLabelPrimaryLang, new PdfTextLocator.LocalisationData(countryOfVaccinationLabelPrimaryLang, 293.337494f, 340.395203f, 422f, 347.425018f)),
                entry(countryOfVaccinationLabelSecondaryLang, new PdfTextLocator.LocalisationData(countryOfVaccinationLabelSecondaryLang, 292.337494f, 350.587311f, 422f, 359.737518f)),
                entry(countryOfVaccinationValuePrimaryLang, new PdfTextLocator.LocalisationData(countryOfVaccinationValuePrimaryLang, 427.812500f, 340.395203f, 570f, 347.425018f)),
                entry(countryOfVaccinationValueSecondaryLang, new PdfTextLocator.LocalisationData(countryOfVaccinationValueSecondaryLang, 427.812500f, 351.587311f, 570f, 357.737518f)),
                entry(uvci, new PdfTextLocator.LocalisationData(uvci, 59f, 333.082703f, 280f, 340.112518f)),
                entry(creationDateTextPrimaryLang, new PdfTextLocator.LocalisationData(creationDateTextPrimaryLang, 59f, 345.151489f, 280f, 350.800018f)),
                entry(creationDateTextSecondaryLang, new PdfTextLocator.LocalisationData(creationDateTextSecondaryLang, 59f, 355.894196f, 280f, 361.375000f)),
                entry(usedDateFormat, new PdfTextLocator.LocalisationData(usedDateFormat, 59f, 366.469208f, 280f, 371.950012f)),
                entry(personalInfoTitlePrimaryLang, new PdfTextLocator.LocalisationData(personalInfoTitlePrimaryLang, 59f, 406.132874f, 570f, 413.500031f)),
                entry(personalInfoTitleTitleSecondaryLang, new PdfTextLocator.LocalisationData(personalInfoTitleTitleSecondaryLang, 59f, 417.849823f, 570f, 424.000031f)),
                entry(nameLabelPrimaryLang, new PdfTextLocator.LocalisationData(nameLabelPrimaryLang, 59f, 442f, 170f, 450f)),
                entry(nameLabelSecondaryLang, new PdfTextLocator.LocalisationData(nameLabelSecondaryLang, 59f, 454f, 170f, 461f)),
                entry(nameValue, new PdfTextLocator.LocalisationData(nameValue, 175f, 442f, 570f, 461f)),
                entry(birthDateLabelPrimaryLang, new PdfTextLocator.LocalisationData(birthDateLabelPrimaryLang, 59f, 475f, 170f, 483f)),
                entry(birthDateLabelSecondaryLang, new PdfTextLocator.LocalisationData(birthDateLabelSecondaryLang, 59f, 487f, 170f, 495f)),
                entry(birthDateValue, new PdfTextLocator.LocalisationData(birthDateValue, 175f, 475f, 570f, 493f)),
                entry(issuerLabelPrimaryLang, new PdfTextLocator.LocalisationData(issuerLabelPrimaryLang, 59f, 528.382690f, 570f, 535.412476f)),
                entry(issuerLabelSecondaryLang, new PdfTextLocator.LocalisationData(issuerLabelSecondaryLang, 59f, 539.574829f, 570f, 545.724976f)),
                entry(issuerValuePrimaryLang, new PdfTextLocator.LocalisationData(issuerValuePrimaryLang, 59f, 555.682861f, 570f, 563.049988f)),
                entry(issuerValueSecondaryLang, new PdfTextLocator.LocalisationData(issuerValueSecondaryLang, 59f, 567.399841f, 570f, 573.549988f)),
                entry(disclaimerValuePrimaryLang, new PdfTextLocator.LocalisationData(disclaimerValuePrimaryLang, 60.950001f, 589.491577f, 564.350037f, 632.875000f)),
                entry(disclaimerValueSecondaryLang, new PdfTextLocator.LocalisationData(disclaimerValueSecondaryLang, 62.187500f, 645.213867f, 563.076172f, 677.950012f)),
                entry(useAppValuePrimaryLang, new PdfTextLocator.LocalisationData(useAppValuePrimaryLang, 59f, 696.607727f, 334.634064f, 703.637512f)),
                entry(useAppValueSecondaryLang, new PdfTextLocator.LocalisationData(useAppValueSecondaryLang, 59f, 707.799866f, 270.285919f, 713.950012f)),
                entry(infoLineValue, new PdfTextLocator.LocalisationData(infoLineValue, 58.250000f, 727.282898f, 262.965454f, 734.650024f)),
                entry(appName, new PdfTextLocator.LocalisationData(appName, 422.975006f, 735.976563f, 481.763245f, 741.625000f))
        );

        public static Collection<PdfTextLocator.LocalisationData> getRegionLocators(String language) {
            switch (language) {
                case "it":
                    Map<String, PdfTextLocator.LocalisationData> deepCopyIT = regionLocatorsMap.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    deepCopyIT.put(disclaimerValuePrimaryLang, new PdfTextLocator.LocalisationData(disclaimerValuePrimaryLang, 59f, 588f, 570f, 628f));
                    deepCopyIT.put(disclaimerValueSecondaryLang, new PdfTextLocator.LocalisationData(disclaimerValueSecondaryLang, 59f, 632f, 570f, 669f));
                    deepCopyIT.put(useAppValuePrimaryLang, new PdfTextLocator.LocalisationData(useAppValuePrimaryLang, 59f, 687f, 335f, 695));
                    deepCopyIT.put(useAppValueSecondaryLang, new PdfTextLocator.LocalisationData(useAppValueSecondaryLang, 59f, 698f, 335f, 705));
                    deepCopyIT.put(infoLineValue, new PdfTextLocator.LocalisationData(infoLineValue, 58f, 717f, 335f, 726f));
                    deepCopyIT.put(appName, new PdfTextLocator.LocalisationData(appName, 422f, 726f, 482f, 733f));
                    return deepCopyIT.values();

                case "fr":
                    Map<String, PdfTextLocator.LocalisationData> deepCopyFR = regionLocatorsMap.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    deepCopyFR.put(appName, new PdfTextLocator.LocalisationData(appName, 416.975006f, 735.976563f, 475.763245f, 741.625000f));
                    return deepCopyFR.values();

                case "rm":
                    Map<String, PdfTextLocator.LocalisationData> deepCopyRM = regionLocatorsMap.entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    deepCopyRM.put(appName, new PdfTextLocator.LocalisationData(appName, 412f, 735.976563f, 472f, 741.625000f));
                    return deepCopyRM.values();

                default:
                    return regionLocatorsMap.values();
            }
        }
    }
}