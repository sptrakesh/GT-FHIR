#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server = 'http://localhost:8080'
@Field def baseUrl = '/base'

String.metaClass.encodeURL =
{
   java.net.URLEncoder.encode delegate, 'UTF-8'
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
      headers.'If-None-Exist' = "${json.resourceType}?name=${json.name}" 
      body = data

      response.success = 
      { 
        resp, reader ->
        println "Create response status: ${resp.statusLine}"
        return resp.getFirstHeader( 'Location' ).value
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
        return json
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

def matchUrl( result, url )
{
  def found = false
  for ( entry in result.entry )
  {
    if ( entry.fullUrl == url )
    {
      found = true
      break
    }
  }
  assert found
}

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()
def org = object.organisation

def orgUrl = create org
println "Created org at url: $orgUrl"
def orgUrl1 = create org
println orgUrl1
assert orgUrl == orgUrl1

def json = read orgUrl
def address = json.address[0].city

println "Searching for entity with name: ${json.name}"
def result = read( "${server}$baseUrl/${json.resourceType}?name=${json.name}" )
assert result.entry.size > 0
matchUrl result, orgUrl

println "Searching for entity with city part of address: $address"
result = read( "${server}$baseUrl/${json.resourceType}?address=${address.encodeURL()}" )
assert result.entry.size > 0
matchUrl result, orgUrl

println "Searching for entity with name: ${json.name} and city: $address"
result = read( "${server}$baseUrl/${json.resourceType}?name=${json.name}&address=${address.encodeURL()}" )
assert result.entry.size > 0
matchUrl result, orgUrl

println "Updating enity with type: ${json.resourceType} and id: ${json.id}"
update json

def modified = read orgUrl
assert json.name == modified.name

println "Deleting enity with type: ${json.resourceType} and id: ${json.id}"
delete orgUrl

println "Attempting to read deleted entity"
read orgUrl
