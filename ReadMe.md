# Threaded Grotto Fisher 

A OSBot barbarian fishing script designed to test a java class (PrioritizedReactiveTask.java) that handles the dispatching of multiple threads to poll 
for the activation of specific ingame tasks and additional threads to properly preform the aforementioned ingame actions.

Features:
* ~40k-50k fishing xph, 3-4k strength and agility xph. 
* psuedo-random inventory shift click drop orders. Items dropped in Breadth first traversal order starting from any inventory slot
* random sleeps after inventory is full to simulate AFK fishing.
* stops on feather shortage

Requirments:
* Barbarian fishing unlocked! 58 fishing, 15 agility, and 35 strength + unlocked barbarian rod. 

# Technical Details:

Some ingame activities such as killing bosses require some attacks to be immediately avoided, this may not be possible 
in some instances where the current action (ex: eating to restore heath) is presently executing because in a single threaded workload 
the eating task will block until completion. My solution is to set a up a multithreaded consumer-producer with interrupt safe threads that can be
arbitrarily canceled if a task of critical priority (ex: avoiding a boss's special attack) is in need of execution. 

The shared buffer between the consumer and producer is a Priority Queue containing PrioritizedReactiveTask instances ordered by that instance's 
priority value (an enum with a HIGH and LOW value). This is to ensure that tasks of higher priority (ex: dodge special attack) are always executed first over lower prioritized tasks. 

The PrioritizedReactiveTask contains 2 abstract methods
* task(): defines a series of ingame action to do. (ex: DropTask, a subclass empties the inventory of fish.) Executed by the Consumer.
* shouldTaskActivate(): defines when task() should be ran (ex: DropTask should activate when the inventory is full.) Executed by the Producer.

On the script's start 2 threads are started, one for checking DropTask's and FishingTask's activation condition.
If the activation condition met, enqueue the respective task into a PriorityQueue.

On each onLoop() call the next task is polled from the PriorityQueue and another thread is used to execute the task. 

The onLoop() then waits synchronously until the separate thread finishes or if the top of the PQ contains a higher priority task. Also if the PQ is empty do nothing. 


