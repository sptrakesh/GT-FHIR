#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server
@Field def baseUrl

String.metaClass.encodeURL =
{
   java.net.URLEncoder.encode delegate, 'UTF-8'
}

def create( data, object )
{
  try
  {
    def json = new JsonSlurper().parseText data
    def http = new HTTPBuilder( server )
    http.request( Method.POST, 'application/json' )
    {
      uri.path = "$baseUrl/${json.resourceType}"
      headers.'If-None-Exist' = "${json.resourceType}?name=${json.name}" 
      body = data

      response.success = 
      { 
        resp, reader ->
        println "Create response status: ${resp.statusLine}"
        return object.fixProtocol( resp.getFirstHeader( 'Location' ).value )
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

def update( org )
{
  try
  {
    org.name = "${org.name} modified"

    def http = new HTTPBuilder( server )
    http.request( Method.PUT, 'application/json' )
    {
      uri.path = "$baseUrl/${org.resourceType}/${org.id}"
      body = new JsonBuilder( org ).toPrettyString()

      response.success = 
      { 
        resp, json ->
        println "Update response status: ${resp.statusLine}"
        println resp.getFirstHeader( 'Location' )
      }

      response.failure =
      {
        resp ->
        println "Update request failed with status ${resp.status}"
      }
    }
  }
  catch ( Throwable t ) { println "$t" }
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

this.server = object.server
this.baseUrl = object.baseUrl
def org = object.organisation

def orgUrl = create org, object
println "Created org at url: $orgUrl"
def orgUrl1 = create org, object
println orgUrl1
assert orgUrl == orgUrl1

def json = object.read orgUrl
def address = json.address[0].city

println "Searching for entity with name: ${json.name}"
def result = object.read( "${server}$baseUrl/${json.resourceType}?name=${json.name}" )
assert result.entry.size > 0
object.matchUrl result, orgUrl

println "Searching for entity with city part of address: $address"
result = object.read( "${server}$baseUrl/${json.resourceType}?address=${address.encodeURL()}" )
assert result.entry.size > 0
object.matchUrl result, orgUrl

println "Searching for entity with name: ${json.name} and city: $address"
result = object.read( "${server}$baseUrl/${json.resourceType}?name=${json.name}&address=${address.encodeURL()}" )
assert result.entry.size > 0
object.matchUrl result, orgUrl

println "Updating enity with type: ${json.resourceType} and id: ${json.id}"
update json

def modified = object.read orgUrl
assert json.name == modified.name

println "Deleting enity with type: ${json.resourceType} and id: ${json.id}"
object.delete orgUrl

println "Attempting to read deleted entity"
object.read orgUrl
