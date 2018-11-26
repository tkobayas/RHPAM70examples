### jBPM71Ex039_customWIH

Example of custom WorkItemHandler

1) Run ProcessJPATest. You will see that MyWorkItemHandler is executed for a Task "MyEmail"

~~~
2018-11-26 12:14:37,958 INFO  [main] [com.sample.MyWorkItemHandler] enter executeWorkItem()
~~~

2) When opening sample.bpmn, you will see "MyEmail" under "Custom Task" category. Confirmed with JBDS 11 (not with JBDS 9. Some environment conditions may affect)
