#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

def patient( object, shell )
{
  def script = shell.parse new File( 'patient.groovy' )
  def url = script.create object.patient
  url = object.fixProtocol url
  println "Created patient at url: $url"
  object.read url
  url
}

def encounter( object, shell, patientId )
{
  def url = object.create object.getEncounter( patientId )
  url = object.fixProtocol url
  println "Created encounter at url: $url"
  object.read url
  url
}

def condition( object, patientId, encounterId )
{
  def condition = object.getCondition patientId, encounterId

  def conditionUrl = object.create condition
  conditionUrl = object.fixProtocol conditionUrl
  println "Created condition at url: $conditionUrl"
  if ( ! conditionUrl ) System.exit 1

  def json = object.read conditionUrl
  def data = new JsonSlurper().parseText condition
  assert json.onsetDateTime.startsWith( data.onsetDateTime )
  assert json.patient.reference == data.patient.reference
  assert json.encounter.reference == data.encounter.reference

  println "Deleting condition with type: ${json.resourceType} and id: ${json.id}"
  object.delete conditionUrl

  println "Attempting to read deleted condition"
  object.read conditionUrl
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl

def shell = new GroovyShell()

def patientUrl = patient object, shell
def patientId = object.getEntityId patientUrl

def encounterUrl = encounter object, shell, patientId
def encounterId = object.getEntityId encounterUrl

try
{
  condition object, patientId, encounterId
}
finally
{
  println "Deleting encounter at URL: $encounterUrl"
  object.delete encounterUrl

  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}
