kn0de
=====

This is a reddit clone I'm building using the Play! Framework and Scala.  It's still in its (very) early stages.

The plan is to use Akka actors to move as much work as possible out of the critical path for handling a user action and returning a result.  Vote calculation, link ranking, etc. will all be handled by actors.  Recently active threads will be stored in a Redis cache allowing RDBMS work to be moved out of the critical path as well.  A PostgreSQL DB will be the "point of truth" in the end with Redis storing votes and rapidly-changing data which will get written periodically to the backend database.

PJAX will be used to decrease render times so that only the discussion content area itself has to be refreshed, not the whole page, while still degrading gracefully on old browsers which will simply do a full refresh.
