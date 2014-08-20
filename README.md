#High-5#
======
High-5 is a widget which learns about how users use their phone, when/where/how they launch their apps. 
And show user the 5 most possible apps in the widget.

Functions are:
* Record system usage
* Predict user intention of app
* Show app usage data in charts
* Configure ignores and logs

High-5 uses naive bayes classifier to identify which app is more likely to be used. All static features 
are configurable via tables.xml. Each feature is stored and represented by a table in sqlite. All data 
accessed via orm objects, class<? extends Table>.

###Packages:###
--------

* android.support.v4.preference: copied preferencefragment from [v4 compatible preference fragment](https://github.com/kolavar/android-support-v4-preferencefragment)

* gt.high5: Application for initialize resources
* gt.high5.activity: UI related classes
  * gt.high5.activity: Activity, broadcast reciever and UI related utils
  * gt.high5.activity.fragment: Fragments
  * gt.high5.activity.widget: AppWidget implementation.
* gt.high5.chart: Record data chart classes
  * gt.high5.chart.core: Record chart core functions, utils and base classes
  * gt.high5.chart.filler: Strategies for filling up chart for different records. Each record table will have a data filler to display chart, can be configured in xml.
* gt.high5.core: Data access interface for UI
  * gt.high5.core.provider: Providers for getting current package information. Can be extended by adding 
    a subclass of PackageProvider and add an object to the PackageProvider.priority array(the first 
    available one will be used).
  * gt.high5.core.service: Reader and Writers for db file and preference.
* gt.high5.database: Database interface, ORM utils
  * gt.high5.database.accessor: Overall database accessors for changing data in database.
  * gt.high5.database.filter: For initializing default package filters to init ignore set. Can be extended
    by adding a subclass of Filter and add element in R.xml.filters
  * gt.high5.database.model: baseclass annotation and utils for ORM tables.
  * gt.high5.database.table: actual tables in database. Can be extended by adding a subclass of Table/ RecordTable/ 
    SimpleRecordTable and add element in R.xml.tables.
    
###Special thanks to the open source libraries:###
---------
  * [android-xlog, log to file](https://github.com/curioustechizen/android-xlog)
  * [android plot, chart library](http://androidplot.com/)
  * [v4 compatible preference fragment](https://github.com/kolavar/android-support-v4-preferencefragment)
