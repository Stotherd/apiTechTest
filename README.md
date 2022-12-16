### apiTechTest

Created in intelliJ using Maven dependencies.

## Assumptions
Assumed we don't have full control over the environment, and tests may be run in the same environment, at the same time. Attempted to ensure concurrency can happen.

Assumed for the cases where we are looking for an ID that doesn't exist that Investigation "1" does not exist, however we could delete the setup created investigation and use that ID.

## Test failures
Currently some tests fail. They do not conform to the schema, either returning an incorrect error code based on the schema, or accepting invalid data and not returning an expected error.
