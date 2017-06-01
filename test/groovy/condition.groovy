#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server = 'http://localhost:8080'
@Field def baseUrl = '/base'

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

def patient( object, shell )
{
  def script = shell.parse new File( 'patient.groovy' )
  def url = script.create object.patient
  println "Created patient at url: $url"
  read url
  url
}

def encounter( object, shell, patientId )
{
  def script = shell.parse new File( 'encounter.groovy' )
  def url = script.create object.getEncounter( patientId )
  println "Created encounter at url: $url"
  read url
  url
}

def condition( object, patientId, encounterId )
{
  def condition = object.getCondition patientId, encounterId

  def conditionUrl = create condition
  println "Created condition at url: $conditionUrl"
  if ( ! conditionUrl ) System.exit 1

  def json = read conditionUrl
  def data = new JsonSlurper().parseText condition
  assert json.onsetDateTime.startsWith( data.onsetDateTime )
  assert json.patient.reference == data.patient.reference
  assert json.encounter.reference == data.encounter.reference

  println "Deleting condition with type: ${json.resourceType} and id: ${json.id}"
  delete conditionUrl

  println "Attempting to read deleted condition"
  read conditionUrl
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

def shell = new GroovyShell()

def patientUrl = patient object, shell
def patientId = getEntityId patientUrl

def encounterUrl = encounter object, shell, patientId
def encounterId = getEntityId encounterUrl

try
{
  condition object, patientId, encounterId
}
finally
{
  println "Deleting encounter at URL: $encounterUrl"
  delete encounterUrl

  println "Deleting patient at URL: $patientUrl"
  delete patientUrl
}
