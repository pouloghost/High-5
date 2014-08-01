<pre>High-5
======
High-5 is a widget which learns about how users use their phone, when/where/how they launch their apps. 
And show user the 5 most possible apps in the widget.

High-5 uses naive bayes classifier to identify which app is more likely to be used. All static features 
are configurable via tables.xml. Each feature is stored and represented by a table in sqlite. All data 
accessed via orm objects, class<? extends Table>.

Packages:
  gt.high5:Application for initialize resources
  
  gt.high5.activity: UI
  
  gt.high5.core.provider: Providers for getting current package information. Can be extended by adding 
    a subclass of PackageProvider and add an object to the PackageProvider.priority array(the first 
    available one will be used).
  gt.high5.core.service: Reader and Writers for db and preference.
  gt.high5.core.widget: AppWidget implementation.
  
  gt.high5.database.accessor: overall database accessors for changing data in database.
  gt.high5.database.filter: for initializing default package filters to init ignore set. Can be extended
    by adding a subclass of Filter and add element in R.xml.filters
  gt.high5.database.model: baseclass annotation and utils for ORM tables.
  gt.high5.database.tables: actual tables in database. Can be extended by adding a subclass of Table/ RecordTable/ 
    SimpleRecordTable and add element in R.xml.tables.
</pre>
