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
  json.name[0].each
  {
    switch ( it.key )
    {
      case 'family': name.family = it.value[0]; break
      case 'given': name.given = it.value[0]; break
    }
  }
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
      headers.'If-None-Exist' = "${json.resourceType}?name=${json.name[0].text}" 
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

def update( patient )
{
  def status = 404

  try
  {
    patient.name[0].each
    {
      if ( 'given' == it.key ) it.value[0] = "${it.value[0]} modified"
    }

    def http = new HTTPBuilder( server )
    http.request( Method.PUT, 'application/json' )
    {
      uri.path = "$baseUrl/${patient.resourceType}/${patient.id}"
      body = new JsonBuilder( patient ).toPrettyString()

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

def patient = 
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
      "text": "SANTIAGO KUROWSKI",
      "family": [
        "KUROWSKI"
      ],
      "given": [
        "SANTIAGO"
      ]
    },
    {
      "family": [
        "Parker"
      ],
      "given": [
        "Austin"
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
        "1318 Rebecca Freeway"
      ],
      "city": "North Attleborough",
      "state": "MA",
      "postalCode": "02761"
    }
  ],
  "maritalStatus": {
    "coding": [
      {
        "system": "http://hl7.org/fhir/v3/MaritalStatus",
        "code": "M"
      }
    ]
  }
}
'''

def patientUrl = create patient
println "Created patient at url: $patientUrl"
if ( ! patientUrl ) System.exit 1

def patientUrl1 = create patient
println patientUrl1
assert patientUrl == patientUrl1

def json = read patientUrl
def name = parseName json

println "Searching for patient with name: $name"
def result = read( "${server}$baseUrl/${json.resourceType}?name=${name.toString().encodeURL()}" )
assert result.entry.size > 0
assert result.entry[0].fullUrl == patientUrl

println "Updating patient with type: ${json.resourceType} and id: ${json.id}"
if ( update( json ) == 500 ) System.exit 2

def modified = read patientUrl
assert json.name == modified.name

println "Deleting patient with type: ${json.resourceType} and id: ${json.id}"
delete patientUrl

println "Attempting to read deleted patient"
read patientUrl
