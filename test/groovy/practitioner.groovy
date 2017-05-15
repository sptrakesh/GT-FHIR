#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

String.metaClass.encodeURL =
{
   java.net.URLEncoder.encode( delegate, 'UTF-8' )
}

class Name
{ 
  String given
  String family 
  String toString() { "$given $family" }
}

server = 'http://localhost:8080'
baseUrl = '/gt-fhir-webapp/base'

def parseName( json )
{
  def name = new Name()
  name.given = json.name.given[0]
  name.family = json.name.family[0]
  name
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
      headers.'If-None-Exist' = "${json.resourceType}?name=${json.name.text}"
      body = data

      response.success = 
      { 
        resp, reader ->
        println "Create response status: ${resp.statusLine}"
        resp.getFirstHeader( 'Location' ).value
      }

      response.failure =
      { 
        resp ->
        println "Create request failed with status ${resp.status}"
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
        json
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

def update( practitioner )
{
  def status = 404

  try
  {
    practitioner.name.given[0] = "${practitioner.name.given[0]} modified"

    def http = new HTTPBuilder( server )
    http.request( Method.PUT, 'application/json' )
    {
      uri.path = "$baseUrl/${practitioner.resourceType}/${practitioner.id}"
      body = new JsonBuilder( practitioner ).toPrettyString()

      response.success = 
      { 
        resp, json ->
        status = resp.status
        println "Update response status: ${resp.statusLine}"
        println resp.getFirstHeader( 'Location' )
      }

      response.failure =
      {
        resp ->
        status = resp.status
        println "Update request failed with status ${resp.status}"
      }
    }
  }
  catch ( Throwable t ) { println "$t" }

  status
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

def practitioner =
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
    "text": "Dokter Bronsig",
    "family": [
      "Bronsig"
    ],
    "given": [
      "Arend"
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
      "city": "Den helder",
      "postalCode": "2333ZA",
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
}
'''

def practitionerUrl = create practitioner
println "Created practitioner at url: $practitionerUrl"
if ( ! practitionerUrl ) System.exit 1

def practitionerUrl1 = create practitioner
assert practitionerUrl == practitionerUrl1

def json = read practitionerUrl
def name = parseName json

println "Searching for practitioner with name: $name"
def result = read( "${server}$baseUrl/${json.resourceType}?name=${name.toString().encodeURL()}" )
assert result.entry.size > 0
assert result.entry[0].fullUrl == practitionerUrl

println "Updating practitioner with type: ${json.resourceType} and id: ${json.id}"
def modified = read practitionerUrl
assert json.name == modified.name

println "Deleting practitioner with type: ${json.resourceType} and id: ${json.id}"
delete practitionerUrl

println "Attempting to read deleted practitioner"
read practitionerUrl
