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

  def msUrl = object.create ms
  msUrl = object.fixProtocol msUrl
  println "Created MedicationStatement at url: $msUrl"
  if ( ! msUrl ) System.exit 1

  def json = object.read msUrl
  def data = new JsonSlurper().parseText ms
  assert json.patient.reference == data.patient.reference
  assert json.supportingInformation[0].reference == data.supportingInformation[0].reference
  assert json.effectivePeriod.start == data.effectivePeriod.start
  assert json.effectivePeriod.end == data.effectivePeriod.end

  println "Deleting MedicationStatement with type: ${json.resourceType} and id: ${json.id}"
  object.delete msUrl

  println "Attempting to read deleted MedicationStatement"
  object.read msUrl
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl

def shell = new GroovyShell()

def patientUrl = patient object, shell
def patientId = object.getEntityId patientUrl

def encounterUrl = encounter object, patientId
def encounterId = object.getEntityId encounterUrl

try
{
  medicationStatement object, patientId, encounterId
}
finally
{
  println "Deleting encounter at URL: $encounterUrl"
  object.delete encounterUrl

  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}
