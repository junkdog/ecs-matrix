## ecs-matrix

A small command-line tool for generate a matrix describing references between [ECS][ecs] classes in your game.
Supports [artemis-odb][artemis] and [ashley][ashley]. Note that artemis-odb also provides this functionality
via its maven, gradle and cli tool.

  [ecs]: http://en.wikipedia.org/wiki/Entity_component_system
  [artemis]: https://github.com/junkdog/artemis-odb
  [ashley]: https://github.com/libgdx/ashley


[![Shaman's Weirding Game: Component Dependency Matrix](https://raw.githubusercontent.com/wiki/junkdog/artemis-odb/images/cdm.png)](http://junkdog.github.io/matrix.html)


### Building
```
$ mvn clean package
```

### Usage 

```
$ java -jar matrix-cli/target/matrix-cli-0.1.0-SNAPSHOT.jar 
Usage: matrix-cli-<version>.jar [options]
  Options:
  * -c, --class-folder
       Root class folder
    -h, --help
       Displays this help message.
       Default: false
    -l, --label
       Project name, used as page title
       Default: Unnamed project
    -o, --output
       Save to file, destination may be given as a folder path
       Default: matrix.html

```

#### Example
```
$ cd ashley-superjumper/
$ java -jar /path/to/matrix-cli-0.1.0-SNAPSHOT.jar \
> -c core/build/classes/main/ \
> -l "Ashley SuperJumper"
Found ECS framework: ashley
$ ls matrix.html 
matrix.html
```
