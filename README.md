# GS Source HTTP

This is a [source](http://graphstream-project.org/doc/Tutorials/Getting-Started/) for GraphStream that allows to control a graph from a web browser. Control is done calling the following url : `http://host/graphId` this is a rest api so you have to provide information for all parameters e.g. `:id`

- `/node` with the following http requests
  - `/:id` post: add a Node</li>
  - `/:id` delete: delete a Node</li>
- `/edge` with the following http requests
  - `/:id/:from/:to/:directed` post: add Edge</li>
  - `/:id` delete: add Edge</li>
- `/step/:step` post: take given steps</li>

## dependencies

the `gs-core` dependency is marked as `"provided"` in the maven configuration, so don't forget to add gs-core to any project's dependencies that would use `gs-source-http`.

## License

See COPYING.