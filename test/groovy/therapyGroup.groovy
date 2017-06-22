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
  def therapyGroup = object.getTherapyGroup patientId
  def url = object.create therapyGroup
  url = object.fixProtocol url
  println "Created TherapyGroup at url: $url"

  def json = object.read url
  def original = new JsonSlurper().parseText therapyGroup
  assert original.subject.reference == json.subject.reference
  assert original.code.coding[0].code == json.code.coding[0].code
  assert original.extension[0].url == json.extension[0].url
  assert original.extension[0].extension[0].url == json.extension[0].extension[0].url
  assert original.extension[0].extension[0].valueString == json.extension[0].extension[0].valueString
  assert original.extension[0].extension[1].url == json.extension[0].extension[1].url
  assert original.extension[0].extension[1].valueInteger == json.extension[0].extension[1].valueInteger

  println "Deleting enity with type: ${json.resourceType} and id: ${json.id}"
  object.delete url

  println "Attempting to read deleted entity"
  object.read url
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl

def shell = new GroovyShell()
def patientUrl = patient object, shell

try
{
  def patientId = object.getEntityId patientUrl
  therapyGroup object, patientId
}
finally
{
  println "Deleting patient at URL: $patientUrl"
  object.delete patientUrl
}
