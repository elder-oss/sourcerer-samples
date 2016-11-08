### About Elder

Elder is a UK based introductory care agency that uses technology to rethink in-home care for
elderly, for more information, see [www.elder.org](http://www.elder.org/?utm_source=github).
For questions about Elder open source software or technology at Elder in general, please contact
tech at elder dot org.

# Sourcerer

For information about the Sourcerer framework, see the [sourcerer GitHub repository](adsfas)

## TODO Sourcerer Sample 

The TODO sample contains an implementation of a simple TODO service, supporting creation, assignment
and completion tracking of TODO items. There is no UI, the recommended way of exploring the services
is to access the built in Swagger UI (on /swagger-ui.html) and call the endpoints through there.

The sample is not intended to demonstrate best practice for production code (for example, there is
no authentication), but rather to show off some of the building blocks in Sourcerer by implementing
a simple event sourced data model with multiple query models derived off a single event stream.

### Infratructure Dependencies

The TODO sample needs an event repository backend, and by default uses EventStore through the ESJC
client. As such, an EventStore server needs to be available, by default expected to be running on
localhost at the default port of 1113.

For creating a persisted query model, the defaults use an H2 instance backed by a file, there are
no requirements for running a "real" database server.

### TODO Command Service

The command service is the only service that can create and modify TODO items. Start it and take
a look at the Swagger UI at http://localhost:8801/swagger-ui.html to see supported operations.
 
### TODO Materializer
 
The materializer is responsible for updating a persisted query model in a relational database, used
by the query service (see below). It opens a H2 web UI that can be used to directly inspect the
generated data model.

The materialier uses a Sourcerer subscription to update the persisted query model with data from
events as these are emitted.

### TODO Query Service

The query service reads data from the persisted query model only, and has no direct dependencies
on the events.
