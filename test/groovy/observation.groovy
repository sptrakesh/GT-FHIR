#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
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

def measurement( object, patientId, encounterId )
{
  def measurement = object.getMeasurement patientId, encounterId
  def measurementUrl = object.create measurement
  measurementUrl = object.fixProtocol measurementUrl
  println "Created measurement at url: $measurementUrl"
  if ( ! measurementUrl ) System.exit 1

  def json = object.read measurementUrl
  def original = new JsonSlurper().parseText measurement
  assert original.code.coding[0].code == json.code.coding[0].code

  println "Deleting measurement with type: ${json.resourceType} and id: ${json.id}"
  object.delete measurementUrl
}

def gradedMeasurement( object, patientId, encounterId )
{
  def measurement = object.getGradedMeasurement patientId, encounterId
  def measurementUrl = object.create measurement
  measurementUrl = object.fixProtocol measurementUrl
  println "Created graded measurement at url: $measurementUrl"
  if ( ! measurementUrl ) System.exit 1

  def json = object.read measurementUrl
  def original = new JsonSlurper().parseText measurement
  assert original.code.coding[0].code == json.code.coding[0].code
  assert original.valueCodeableConcept.coding[0].code == json.valueCodeableConcept.coding[0].code

  println "Deleting graded measurement with type: ${json.resourceType} and id: ${json.id}"
  object.delete measurementUrl
}

def observation( object, patientId, encounterId )
{
  def observation = object.getObservation patientId, encounterId
  def observationUrl = object.create observation
  observationUrl = object.fixProtocol observationUrl
  println "Created observation at url: $observationUrl"
  if ( ! observationUrl ) System.exit 1

  def json = object.read observationUrl
  def original = new JsonSlurper().parseText observation
  assert original.code.coding[0].code == json.code.coding[0].code
  assert original.effectiveDateTime == json.effectiveDateTime

  println "Deleting observation with type: ${json.resourceType} and id: ${json.id}"
  object.delete observationUrl
}

def numberOfPregnancies( object, patientId, encounterId )
{
  def observation = object.getNumberOfPregnancies patientId, encounterId, 3
  def observationUrl = object.create observation
  observationUrl = object.fixProtocol observationUrl
  println "Created pregnancy observation at url: $observationUrl"
  if ( ! observationUrl ) System.exit 1

  def json = object.read observationUrl
  def original = new JsonSlurper().parseText observation
  assert original.code.coding[0].code == json.code.coding[0].code
  assert original.valueString == json.valueString
  assert original.effectiveDateTime == json.effectiveDateTime

  println "Deleting pregnancy observation with type: ${json.resourceType} and id: ${json.id}"
  object.delete observationUrl
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
  measurement object, patientId, encounterId
  gradedMeasurement object, patientId, encounterId
  observation object, patientId, encounterId
  numberOfPregnancies object, patientId, encounterId
}
finally
{
  println "Deleting encounter at URL: $encounterUrl"
  object.delete encounterUrl

  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}

