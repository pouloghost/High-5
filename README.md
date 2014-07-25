High-5
======
High-5 is a widget which learns about how users use their phone, when/where/how they launch their apps. And show user the 5 most possible apps in the widget.

High-5 uses naive bayes classifier to identify which app is more likely to be used. All static features are configurable via tables.xml. Each feature is stored and represented by a table in sqlite. All data accessed via orm objects, class<? extends Table>.
