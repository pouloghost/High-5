#High-5#
======
High-5 is a widget which learns about how users use their phone, when/where/how they launch their apps. And show user top 5 apps the user may want to use in the widget. High-5 widget serves as a supplement 
for the task manager, who works only based on what has been done LRU way.

Functions are:
* Record system usage
* Predict user intention of app
* Show app usage data in charts
* Configure ignores and logs

High-5 uses changeable predictor to identify which app is more likely to be used. All static features are configurable via xml. Each feature is stored and represented by a table in sqlite. All data 
accessed via orm objects, class<? extends Table>.

###Packages:###
--------

* android.support.v4.preference: copied preference fragment from [v4 compatible preference fragment](https://github.com/kolavar/android-support-v4-preferencefragment).

* gt.high5: Application for initialize resources:
* gt.high5.activity: UI related classes:
  * gt.high5.activity: Activity, broadcast receiver and UI related utils.
  * gt.high5.activity.fragment: Fragments.
  * gt.high5.activity.widget: AppWidget implementation.
* gt.high5.chart: Record data chart classes:
  * gt.high5.chart.core: Record chart core functions, utils and base classes.
  * gt.high5.chart.filler: Strategies for filling up chart for different records. Each record table will have a data filler to display chart, can be configured in xml attribute filler in table tag.
* gt.high5.core: Data access interface for UI:
  * gt.high5.core.provider: Providers for getting current package information. Can be extended by adding a subclass of PackageProvider and add an object to the PackageProvider.priority array(the first available one will be used).
  * gt.high5.core.service: Reader and Writers for persistent storages(preferences, db, file).
  * gt.high5.core.predictor: Strategies used to predict based on records. This is the determining factor that changes databases, xml configurations, record types, efficiency and final outcome:
  	* .collaborativefilter: cf predictor related classes.
  	* .naivebayes: naive bayes predictor related classes.
* gt.high5.database: Database interface, ORM utils:
  * gt.high5.database.accessor: Overall database accessors for changing data in database. Associated database is defined by predictor.
  * gt.high5.database.filter: For initializing default package filters to init ignore set. Can be extended by adding a subclass of Filter and add element in R.xml.filters
  * gt.high5.database.model: base class, annotation and utils for ORM tables.
  * gt.high5.database.raw: record a certain number of raw records in a round robin way. All available system runtime details is only accessible from RawRecord.
  * gt.high5.database.table: actual tables in database. Can be extended by adding a subclass of Table, RecordTable or SimpleRecordTable and add element in R.xml.tables.
    
###Special thanks to the open source libraries:###
---------
  * [android-xlog, log to file](https://github.com/curioustechizen/android-xlog)
  * [android plot, chart library](http://androidplot.com/) for branch androidplot
  * [achartengine, chart library](http://achartengine.org/) for branch achartengine, this is significently more robust than androidplot.
  * [v4 compatible preference fragment](https://github.com/kolavar/android-support-v4-preferencefragment)
