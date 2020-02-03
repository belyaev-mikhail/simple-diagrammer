# simple-diagrammer
A simple library that turns ascii diagrams into images

Turns dis:

```
         +~~~~~~~~~~~~~~~~~+
         :                 :
         :   $1 = eval x   :
         :                 :
         +~~~~~+~~~~~+~~~~~+
               |     |
        +------+     +-------+
        v                    v
+-------+-------+   +--------+-------+
|               |   |                |
|   assume $1   |   |   assume !$1   |
|               |   |                |
+-------+-------+   +--------+-------+
        |                    |
        v                    v
 +------+------+    +~~~~~~~~+~~~~~~~~+
 |             |    :                 :
 |   $3 = $1   |    :   $2 = eval y   :
 |             |    :                 :
 +------+------+    +~~~~~~~~+~~~~~~~~+
        |                    |
        |                    v
        |             +------+------+
        |             |             |
        |             |   $3 = $2   |
        |             |             |
        |             +------+------+
        |                    |
        |      +-------------+
        v      v
  +-----+------+-----+
  |                  |
  |   $result = $3   |
  |                  |
  +------------------+

```

into dis:


![GitHub Logo](/examples/Or.svg)

See [examples](/examples/) for more examples
