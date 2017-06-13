import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

class Data
{
  String server = System.getProperty 'server', 'http://localhost:8080'
  String baseUrl = '/base'

  String fixProtocol( url )
  {
   ( server.startsWith( 'https://') ) ? url.replace( 'http://', 'https://' ) : url
  }

  def getEntityId( entityUrl )
  {
    def parts = entityUrl.split '/'
    parts[parts.length - 1]
  }

  def create( data )
  {
    try
    {
      def json = new JsonSlurper().parseText data
      def http = new HTTPBuilder( server )
      http.request( Method.POST, 'application/json' )
      {
        uri.path = "$baseUrl/${json.resourceType}"
        body = data

        response.success =
        {
          resp, reader ->
            println "Create response status: ${resp.statusLine}"
            resp.getFirstHeader( 'Location' ).value
        }

        response.failure =
        {
          resp -> println "Create request failed with status ${resp.status}"
        }
      }
    }
    catch ( Throwable t ) { println "$t" }
  }

  def read( url )
  {
    try
    {
      def http = new HTTPBuilder( url )
      http.request( Method.GET, 'application/json' )
      {
        response.success =
        {
          resp, json ->
            println "Read json with type: ${json.resourceType} and id: ${json.id}"
            return json
        }

        response.failure =
        {
          resp ->
            println "read request failed with status ${resp.status}"
        }
      }
    }
    catch ( Throwable t ) { println "$url\n$t" }
  }

  def delete( url )
  {
    try
    {
      def http = new HTTPBuilder( url )
      http.request( Method.DELETE )
      {
        response.success =
        {
          resp, data ->
            println "Delete response status: ${resp.statusLine}"
        }

        response.failure =
        {
          resp ->
            println "Delete request failed with status ${resp.status}"
        }
      }
    }
    catch ( Throwable t ) { println "$url\n$t" }
  }

  def matchUrl( result, url )
  {
    def found = false
    for ( entry in result.entry )
    {
      if ( fixProtocol( entry.fullUrl ) == url )
      {
        found = true
        break
      }
    }
    assert found
  }

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
      "postalCode": "11111",
      "country": "US"
    }
  ]
}'''
  }

  String getPatient()
  {
'''{
  "resourceType": "Patient",
  "name": [
    {
      "use": "official",
      "text": "UnitTest User",
      "family": [ "User" ],
      "given": [ "UnitTest" ]
    }
  ],
  "gender": "female",
  "birthDate": "1937-05-14",
  "address": [
    {
      "line": [ "1111 Unit Test Street" ],
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
  "deceasedBoolean": true,
  "extension": [
    {
      "url": "http://hl7.org/fhir/StructureDefinition/us-core-race",
      "valueCodeableConcept": {
        "text": "Asian",
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "413582008",
            "display": "Asian"
          }
        ]
      }
    },
    {
      "url": "http://hl7.org/fhir/StructureDefinition/us-core-ethnicity",
      "valueCodeableConcept": {
        "text": "Not Hispanic or Latino",
        "coding": [
          {
            "system": "2.16.840.1.113883.5.50",
            "code": "2186-5",
            "display": "Not Hispanic or Latino"
          }
        ]
      }
    },
    {
      "url": "http://hl7.org/fhir/StructureDefinition/us-core-birth-sex",
      "valueCodeableConcept": {
        "text": "Male",
        "coding": [
          {
            "system": "http://hl7.org/fhir/v3/AdministrativeGender",
            "code": "M",
            "display": "Male"
          }
        ]
      }
    }
  ]
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

  String getEncounter( patientId )
  {
"""{
  "resourceType": "Encounter",
  "meta": {
    "versionId": "1",
    "lastUpdated": "2017-03-26T11:37:52.119-04:00"
  },
  "status": "finished",
  "class": {
    "code": "outpatient"
  },
  "type": [
    {
      "coding": [
        {
          "system": "http://snomed.info/sct",
          "code": "185349003"
        }
      ],
      "text": "Outpatient Encounter"
    }
  ],
  "patient": {
    "reference": "Patient/$patientId"
  },
  "period": {
    "start": "2014-10-01T02:46:18-04:00",
    "end": "2014-10-01T03:01:18-04:00"
  }
}
"""
  }

  String getMeasurement( patientId, encounterId )
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
  "context": {
    "reference": "Encounter/$encounterId"
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

  String getGradedMeasurement( patientId, encounterId )
  {
"""{
  "resourceType": "Observation",
  "meta": {
    "versionId": "2",
    "lastUpdated": "2017-05-15T11:56:28.904-04:00"
  },
  "status": "final",
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "373375007"
      }
    ]
  },
  "subject": {
    "reference": "Patient/$patientId"
  },
  "context": {
    "reference": "Encounter/$encounterId"
  },
  "effectiveDateTime": "2017-02-03T13:23:33.000-05:00",
  "valueCodeableConcept": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "258244004"
      }
    ]
  }
}"""
  }

  String getObservation( patientId, encounterId )
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
        "code": "exam"
      }
    ]
  },
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "85956000",
        "display": "Adenoid squamous cell carcinoma"
      }
    ]
  },
  "subject": {
    "reference": "Patient/$patientId"
  },
  "context": {
    "reference": "Encounter/$encounterId"
  },
  "component": [
    {
      "code": {
        "coding": [
          {
            "system": "urn:oid:2.16.840.1.113883.6.96",
            "code": "85956000",
            "display": "Adenoid squamous cell carcinoma"
          }
        ]
      }
    },
    {
      "code": {
        "coding": [
          {
            "system": "http://snomed.info/sct",
            "code": "373375007",
            "display": "Well differentiated histological grade finding"
          }
        ]
      },
      "valueString": "Grade 1 (well differentiated)"
    }
  ],
  "effectiveDateTime": "2017-03-07T23:50:16+00:00"
}"""
  }

  String getNumberOfPregnancies( patientId, encounterId, number )
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
        "system": "http://snomed.info/sct",
        "code": "161732006",
        "display": "Gravida"
      }
    ]
  },
  "valueString": "$number",
  "subject": {
    "reference": "Patient/$patientId"
  },
  "context": {
    "reference": "Encounter/$encounterId"
  },
  "effectiveDateTime": "2017-03-07T23:50:16+00:00"
}"""
  }

  String getCondition( patientId, encounterId )
  {
"""{
  "resourceType": "Condition",
  "patient": {
    "reference": "Patient/$patientId"
  },
  "encounter": {
    "reference": "Encounter/$encounterId"
  },
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "363402007"
      }
    ]
  },
  "category": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "39154008"
      }
    ]
  },
  "clinicalStatus": "active",
  "verificationStatus": "confirmed",
  "onsetDateTime": "2014-01-01"
}"""
  }

  String getMedicationStatement( patientId, encounterId )
  {
"""{
  "resourceType": "MedicationStatement",
  "status": "active",
  "patient": {
    "reference": "Patient/$patientId"
  },
  "supportingInformation": [
    { "reference": "Encounter/$encounterId" }
  ],
  "medicationCodeableConcept": {
    "coding": [
      {
        "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
        "code": "4492"
      }
    ]
  },
  "effectivePeriod": {
    "start": "2014-01-01",
    "end": "2014-01-05"
  }
}"""
  }

  String getProcedure( patientId, encounterId ) {
"""{
  "resourceType": "Procedure",
  "status": "completed",
  "code": {
    "coding": [
      {
        "system": "http://snomed.info/sct",
        "code": "33195004"
      }
    ]
  },
  "subject": {
    "reference": "Patient/$patientId"
  },
  "encounter": {
    "reference": "Encounter/$encounterId"
  },
  "performedDateTime": "2016-06-28T15:50:29+00:00"
}
"""
  }
}