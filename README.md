# ratelimiting

This is a simple application on OpenDaylight. It implement a RPC,
which builds up a path from port A to port B in the same switch with the given rate limiting and burst size.
And you could test it functionality with [CPqD Mininet](https://github.com/CPqD/ofsoftswitch13/wiki/OpenFlow-1.3-Tutorial).

This is a good material for ODL beginners. After reading this source code, you could learn

+  how to add a flow into a switch you give
+  how to add a meter into a switch you give
+  how to connect a meter with a flow so that limit rate of this flow

If you have any questions, please feel free to contact me x.shawn.lin@gmail.com.
