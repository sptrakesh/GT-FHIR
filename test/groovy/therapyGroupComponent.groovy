#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.transform.Field
import groovy.json.JsonSlurper

@Field def server = System.getProperty 'server', 'http://localhost:8080'
@Field def baseUrl = '/base'

def patient( object, shell )
{
  def script = shell.parse new File( 'patient.groovy' )
  def url = script.create object.patient
  url = object.fixProtocol url
  println "Created patient at url: $url"
  object.read url
  url
}

def therapyGroup( object, patientId )
{
  def url = object.create object.getTherapyGroup( patientId )
  url = object.fixProtocol url
  println "Created TherapyGroup at url: $url"
  object.read url
  url
}

def encounter( object, patientId )
{
  def url = object.create object.getEncounter( patientId )
  url = object.fixProtocol url
  println "Created encounter at url: $url"
  object.read url
  url
}

def medicationStatement( object, patientId, encounterId )
{
  def ms = object.getMedicationStatement patientId, encounterId
  def url = object.create ms
  url = object.fixProtocol url
  println "Created medication statement at url: $url"
  object.read url
  url
}

def procedure( object, patientId, encounterId )
{
  def ms = object.getProcedure patientId, encounterId
  def url = object.create ms
  url = object.fixProtocol url
  println "Created procedure at url: $url"
  object.read url
  url
}

def observation( object, patientId, encounterId )
{
  def ms = object.getObservation patientId, encounterId
  def url = object.create ms
  url = object.fixProtocol url
  println "Created observation at url: $url"
  object.read url
  url
}

def component( object, therapyGroupId, medicationStatementId )
{
  def component = object.getTherapyGroupComponent therapyGroupId, medicationStatementId
  def url = object.create component
  url = object.fixProtocol url
  println "Created TherapyGroupComponent at url: $url"
  if ( ! url ) System.exit 1

  def json = object.read url
  def data = new JsonSlurper().parseText component
  assert json.subject.reference == data.subject.reference
  assert json.code.coding[0].code == data.code.coding[0].code
  assert json.extension.size == data.extension.size

  println "Deleting TherapyGroupComponent with type: ${json.resourceType} and id: ${json.id}"
  object.delete url

  println "Attempting to read deleted TherapyGroupComponent"
  object.read url
}

def component( object, therapyGroupId, medicationStatementId, procedureId )
{
  def component = object.getTherapyGroupComponent therapyGroupId, medicationStatementId, procedureId
  def url = object.create component
  url = object.fixProtocol url
  println "Created TherapyGroupComponent at url: $url"
  if ( ! url ) System.exit 1

  def json = object.read url
  def data = new JsonSlurper().parseText component
  assert json.subject.reference == data.subject.reference
  assert json.code.coding[0].code == data.code.coding[0].code
  assert json.extension.size == data.extension.size

  println "Deleting TherapyGroupComponent with type: ${json.resourceType} and id: ${json.id}"
  object.delete url

  println "Attempting to read deleted TherapyGroupComponent"
  object.read url
}

def component( object, therapyGroupId, medicationStatementId, procedureId, observationId )
{
  def component = object.getTherapyGroupComponent therapyGroupId, medicationStatementId, procedureId, observationId
  def url = object.create component
  url = object.fixProtocol url
  println "Created TherapyGroupComponent at url: $url"
  if ( ! url ) System.exit 1

  def json = object.read url
  def data = new JsonSlurper().parseText component
  assert json.subject.reference == data.subject.reference
  assert json.code.coding[0].code == data.code.coding[0].code
  assert json.extension.size == data.extension.size

  println "Deleting TherapyGroupComponent with type: ${json.resourceType} and id: ${json.id}"
  object.delete url

  println "Attempting to read deleted TherapyGroupComponent"
  object.read url
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl

def shell = new GroovyShell()
def patientUrl = patient object, shell
def patientId = object.getEntityId patientUrl

def therapyGroupUrl = therapyGroup object, patientId
def therapyGroupId = object.getEntityId therapyGroupUrl

def encounterUrl = encounter object, patientId
def encounterId = object.getEntityId encounterUrl

def medicationStatementUrl = medicationStatement object, patientId, encounterId
def medicationStatementId = object.getEntityId medicationStatementUrl

def procedureUrl = procedure object, patientId, encounterId
def procedureId = object.getEntityId procedureUrl

def observationUrl = observation object, patientId, encounterId
def observationId = object.getEntityId observationUrl

try
{
  component object, therapyGroupId, medicationStatementId
  component object, therapyGroupId, medicationStatementId, procedureId
  component object, therapyGroupId, medicationStatementId, procedureId, observationId
}
finally
{
  println "Deleting observation at URL: $observationUrl"
  object.delete observationUrl

  println "Deleting procedure at URL: $procedureUrl"
  object.delete procedureUrl

  println "Deleting medication statement at URL: $medicationStatementUrl"
  object.delete medicationStatementUrl

  println "Deleting encounter at URL: $encounterUrl"
  object.delete encounterUrl

  println "Deleting therapy group at URL: $therapyGroupUrl"
  object.delete therapyGroupUrl

  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}
