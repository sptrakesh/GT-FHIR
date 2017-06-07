#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server = System.getProperty 'server', 'http://localhost:8080'
@Field def baseUrl = '/base'

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

def encounter( object, patientId )
{
  def encounter = object.getEncounter patientId
  def encounterUrl = create encounter
  encounterUrl = object.fixProtocol encounterUrl
  println "Created Encounter at url: $encounterUrl"

  def json = object.read encounterUrl

  println "Deleting enity with type: ${json.resourceType} and id: ${json.id}"
  object.delete encounterUrl

  println "Attempting to read deleted entity"
  object.read encounterUrl
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl

def shell = new GroovyShell()
def script = shell.parse new File( 'patient.groovy' )
def patientUrl = script.create object.patient
patientUrl = object.fixProtocol patientUrl
object.read patientUrl

try
{
  def parts = patientUrl.split '/'
  def patientId = parts[parts.length - 1]
  encounter object, patientId
}
finally
{
  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}
