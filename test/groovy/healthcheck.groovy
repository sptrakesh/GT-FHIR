#!/usr/bin/env groovy
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

def sourceFile = new File( 'Data.groovy' )
def cls = new GroovyClassLoader(getClass().classLoader).parseClass sourceFile
def object = (GroovyObject) cls.newInstance()

def server = object.server
def baseUrl = '/healthcheck'

def http = new HTTPBuilder( "$server$baseUrl" )
http.request( Method.GET, 'text/plain' )
{
  response.success =
  {
    resp, data -> println "Read response $data"
  }

  response.failure =
  {
    resp ->
      println "Read request failed with status ${resp.status}"
      System.exit 1
  }
}
