class Data
{
  String getOrganisation()
  {
'''{
  "resourceType": "Organization",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2017-02-20T01:23:47.502-05:00"
  },
  "text": {
    "status": "generated",
    "div": "UnitTest - A DIVISION OF HEALTH GIZMOS CORP"
  },
  "identifier": [
    {
      "system": "www.nationalorgidentifier.gov/ids",
      "value": "UWEARME"
    }
  ],
  "name": "UWEARME",
  "address": [
    {
      "line": [
        "2000 UnitTest DRIVE"
      ],
      "city": "Unit Test",
      "state": "AA",
      "postalCode": "11111"
      "country": "US"
    }
  ]
}'''
  }

  String getPatient()
  {
'''{
  "resourceType": "Patient",
  "meta": {
    "versionId": "5",
    "lastUpdated": "2017-04-28T14:59:13.048-04:00"
  },
  "text": {
    "status": "generated",
    "div": "<div xmlns='http://www.w3.org/1999/xhtml'>SANTIAGO KUROWSKI</div>"
  },
  "name": [
    {
      "use": "official",
      "text": "UnitTest User",
      "family": [
        "User"
      ],
      "given": [
        "UnitTest"
      ]
    },
    {
      "family": [
        "User1"
      ],
      "given": [
        "UnitTest1"
      ]
    }
  ],
  "gender": "female",
  "birthDate": "1937-05-14",
  "address": [
    {
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/geolocation",
          "extension": [
            {
              "url": "latitude",
              "valueDecimal": 41.94717542029152
            },
            {
              "url": "longitude",
              "valueDecimal": -71.35055546377211
            }
          ]
        }
      ],
      "line": [
        "1111 Unit Test Street"
      ],
      "city": "Unit Test",
      "state": "AA",
      "postalCode": "11111"
    }
  ],
  "maritalStatus": {
    "coding": [
      {
        "system": "http://hl7.org/fhir/v3/MaritalStatus",
        "code": "M"
      }
    ]
  },
  "deceasedBoolean": true
}
'''
  }

  String getPractitioner()
  {
'''{
  "resourceType": "Practitioner",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2017-01-07T01:04:56.511-05:00"
  },
  "text": {
    "status": "generated",
    "div": "<div xmlns='http://www.w3.org/1999/xhtml'><p><b>Generated Narrative with Details</b></p><p><b>id</b>: f201</p><p><b>identifier</b>: UZI-nummer = 12345678901 (OFFICIAL)</p><p><b>active</b>: true</p><p><b>name</b>: Dokter Bronsig(OFFICIAL)</p><p><b>telecom</b>: ph: +31715269111(WORK)</p> <p><b>address</b>: Walvisbaai 3 C4 - Automatisering Den helder 2333ZA NLD (WORK)</p>\\n  <p><b>gender</b>: male</p>\\n  <p><b>birthDate</b>: Dec 24, 1956</p>\\n  <h3>Roles</h3>\\n  <table>\\n    <tr>\\n      <td>-</td>\\n      <td>\\n        <b>Organization</b>\\n      </td>\\n      <td>\\n        <b>Code</b>\\n      </td>\\n      <td>\\n        <b>Specialty</b>\\n      </td>\\n    </tr>\\n    <tr>\\n      <td>*</td>\\n      <td>\\n        <a>AUMC</a>\\n      </td>\\n      <td>Implementation of planned interventions <span>(Details : {SNOMED CT code '225304007' = 'Implementation of planned interventions (procedure)', given as 'Implementation of planned interventions'})</span></td>\\n      <td>Medical oncologist <span>(Details : {SNOMED CT code '310512001' = 'Medical oncologist (occupation)', given as 'Medical oncologist'})</span></td>\\n    </tr>\\n  </table>\\n  <h3>Qualifications</h3>\\n  <table>\\n    <tr>\\n      <td>-</td>\\n      <td>\\n        <b>Code</b>\\n      </td>\\n    </tr>\\n    <tr>\\n      <td>*</td>\\n      <td>Pulmonologist <span>(Details : {SNOMED CT code '41672002' = 'Respiratory disease specialist (occupation)', given as 'Pulmonologist'})</span></td></tr></table></div>"
  },
  "identifier": [
    {
      "use": "official",
      "type": {
        "text": "UZI-nummer"
      },
      "system": "urn:oid:2.16.528.1.1007.3.1",
      "value": "12345678901"
    }
  ],
  "active": true,
  "name": {
    "use": "official",
    "text": "Dokter Unit Test",
    "family": [
      "Test"
    ],
    "given": [
      "Unit"
    ],
    "prefix": [
      "Dr."
    ]
  },
  "telecom": [
    {
      "system": "phone",
      "value": "+31715269111",
      "use": "work"
    }
  ],
  "address": [
    {
      "use": "work",
      "line": [
        "Walvisbaai 3",
        "C4 - Automatisering"
      ],
      "city": "Den helder 123",
      "postalCode": "2333ZA11",
      "country": "NLD"
    }
  ],
  "gender": "male",
  "birthDate": "1956-12-24",
  "qualification": [
    {
      "code": {
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "41672002",
            "display": "Pulmonologist"
          }
        ]
      }
    }
  ]
}'''
  }

  String getMeasurement( patientId )
  {
"""{
  "resourceType": "Observation",
  "meta": {
  "versionId": "2",
  "lastUpdated": "2017-05-15T11:56:28.904-04:00"
  },
  "text": {
  "status": "generated",
  "div": "<div xmlns='http://www.w3.org/1999/xhtml'>UW Device for Pat Id # winkler Systolic BP: 128 mmHg on 2017-02-03 13:23:33</div>"
  },
  "identifier": [
  {
    "system": "www.uwearme.com/measures",
    "value": "winkler-2017-02-03-13-23-33-8480-6"
  }
  ],
  "status": "final",
  "code": {
  "coding": [
    {
    "system": "http://loinc.org",
    "code": "8480-6",
    "display": "Systolic BP"
    }
  ]
  },
  "subject": {
  "reference": "Patient/$patientId"
  },
  "effectiveDateTime": "2017-02-03T13:23:33.000-05:00",
  "valueQuantity": {
  "value": 128,
  "unit": "mmHg",
  "system": "http://unitsofmeasure.org",
  "code": "mm[Hg]"
  }
}"""
  }

  String getObservation( patientId )
  {
"""{
  "resourceType": "Observation",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2017-03-07T18:50:16.717-05:00"
  },
  "status": "final",
  "category": {
    "coding": [
      {
        "code": "social-history"
      }
    ]
  },
  "code": {
    "coding": [
      {
        "system": "urn:oid:2.16.840.1.113883.6.96",
        "code": "8392000",
        "display": "SNOMEDCT"
      }
    ]
  },
  "subject": {
    "reference": "Patient/$patientId"
  },
  "valueCodeableConcept": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "8392000",
        "display": "Smoking"
      }
    ],
    "text": "Smoking"
  }
}"""
  }

  String getCondition( patientId )
  {
"""{
  "resourceType": "Condition",
  "meta": {
    "versionId": "2",
    "lastUpdated": "2017-03-16T10:23:39.915-04:00"
  },
  "patient": {
    "reference": "Patient/$patientId",
    "display": "Unit Test"
  },
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "448952004",
        "display": "Infiltrating duct carcinoma of female breast"
      }
    ],
    "text": "IDC"
  },
  "category": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "39154008",
        "display": "Clinical diagnosis"
      }
    ]
  },
  "clinicalStatus": "active",
  "verificationStatus": "confirmed",
  "onsetDateTime": "2014-01-01"
}"""
  }
}