@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1' )
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovy.transform.Field

@Field def server = 'http://localhost:8080'
@Field def baseUrl = '/healthcheck'

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
