#!/usr/bin/env groovy
println 'Testing Healthcheck endpoint'
evaluate new File( 'healthcheck.groovy' )
println 'Testing Organization entity'
evaluate new File( 'org.groovy' )
println 'Testing Patient entity'
evaluate new File( 'patient.groovy' )
println 'Testing Practitioner entity'
evaluate new File( 'practitioner.groovy' )
println 'Testing Encounter entity'
evaluate new File( 'encounter.groovy' )
println 'Testing Observation entity'
evaluate new File( 'observation.groovy' )
println 'Testing Condition entity'
evaluate new File( 'condition.groovy' )
println 'All tests completed'
