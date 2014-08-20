<pre>High-5
======
High-5 is a widget which learns about how users use their phone, when/where/how they launch their apps. 
And show user the 5 most possible apps in the widget.

High-5 uses naive bayes classifier to identify which app is more likely to be used. All static features 
are configurable via tables.xml. Each feature is stored and represented by a table in sqlite. All data 
accessed via orm objects, class<? extends Table>.

Packages:

  android.support.v4.preference: copied preferencefragment from <a href="https://github.com/kolavar/android-support-v4-preferencefragment">v4 compatible preference fragment</a>

  gt.high5: Application for initialize resources
  
  gt.high5.activity: activity, broadcast reciever and UI related utils
  
  gt.high5.activity.fragment: fragments
  
  gt.high5.chart.core: record chart core functions, utils and base classes
  
  gt.high5.chart.filler: strategies for filling up chart for different records
  
  gt.high5.core.provider: Providers for getting current package information. Can be extended by adding 
    a subclass of PackageProvider and add an object to the PackageProvider.priority array(the first 
    available one will be used).
    
  gt.high5.core.service: Reader and Writers for db file and preference.
  
  gt.high5.core.widget: AppWidget implementation.
  
  gt.high5.database.accessor: overall database accessors for changing data in database.
  
  gt.high5.database.filter: for initializing default package filters to init ignore set. Can be extended
    by adding a subclass of Filter and add element in R.xml.filters
    
  gt.high5.database.model: baseclass annotation and utils for ORM tables.
  
  gt.high5.database.table: actual tables in database. Can be extended by adding a subclass of Table/ RecordTable/ 
    SimpleRecordTable and add element in R.xml.tables.
    
    
  special thanks to the open source libraries:
    <a href="https://github.com/curioustechizen/android-xlog">android-xlog, log to file</a>
    <a href="http://androidplot.com/">android plot, chart library</a>
    <a href="https://github.com/kolavar/android-support-v4-preferencefragment">v4 compatible preference fragment</a>
</pre>
