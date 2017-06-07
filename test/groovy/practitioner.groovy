#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

String.metaClass.encodeURL =
{
   java.net.URLEncoder.encode delegate, 'UTF-8'
}

class Name
{ 
  String given
  String family 
  String toString() { "$given $family" }
}

@Field def server
@Field def baseUrl

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

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl
def practitioner = object.practitioner

def practitionerUrl = create practitioner
practitionerUrl = object.fixProtocol practitionerUrl
println "Created practitioner at url: $practitionerUrl"
if ( ! practitionerUrl ) System.exit 1

def practitionerUrl1 = create practitioner
practitionerUrl1 = object.fixProtocol practitionerUrl1
assert practitionerUrl == practitionerUrl1

def json = object.read practitionerUrl
def name = parseName json

println "Searching for practitioner with name: $name"
def result = object.read( "${server}$baseUrl/${json.resourceType}?name=${name.toString().encodeURL()}" )
assert result.entry.size > 0
object.matchUrl result, practitionerUrl

println "Updating practitioner with type: ${json.resourceType} and id: ${json.id}"
def modified = object.read practitionerUrl
assert json.name == modified.name

println "Deleting practitioner with type: ${json.resourceType} and id: ${json.id}"
object.delete practitionerUrl

println "Attempting to read deleted practitioner"
object.read practitionerUrl
