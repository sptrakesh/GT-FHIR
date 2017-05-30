#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server = 'http://localhost:8080'
@Field def baseUrl = '/gt-fhir-webapp/base'

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
          json
      }

      response.failure =
      {
        resp -> println "read request failed with status ${resp.status}"
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
        resp, data -> println "Delete response status: ${resp.statusLine}"
      }

      response.failure =
      {
        resp -> println "Delete request failed with status ${resp.status}"
      }
    }
  }
  catch ( Throwable t ) { println "$url\n$t" }
}

def encounter( object, patientId )
{
  def encounter = object.getEncounter patientId
  def encounterUrl = create encounter
  println "Created Encounter at url: $encounterUrl"

  def json = read encounterUrl

  println "Deleting enity with type: ${json.resourceType} and id: ${json.id}"
  delete encounterUrl

  println "Attempting to read deleted entity"
  read encounterUrl
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

def shell = new GroovyShell()
def script = shell.parse new File( 'patient.groovy' )
def patientUrl = script.create object.patient
read patientUrl

try
{
  def parts = patientUrl.split '/'
  def patientId = parts[parts.length - 1]
  encounter object, patientId
}
finally
{
  println "Deleting patient at URL: $patientUrl"
  script.delete patientUrl
}
