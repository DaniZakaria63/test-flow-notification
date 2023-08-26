# Advanced Test Application

An advanced testing android application to test complicated source with first-hand implementation.

### This project is about 

Technology stack has implemented:
- Espresso, 
- Hilt Application Test,
- MockServer Test,
- Navigation Component Test,
- Room Database Test,
- Notification Test, 
- Data Repository Test,
- Mockito, 
- JUnit4 and Hamcrest Assertion.

Testing MVVM architecture such as ViewModel, Fragment, Activity, Repository, Data Source, and Networking.


### The architecture

Testable and maintenable android architecture is more easier with Hilt. So, basically for functional class inherit their parent interface which represented its functionality. Then, for ViewModel need little bit re-configuration because testing activity can be so tough if don't do so.

Abstract class ViewModel as parent and type that provided inside activity. The activity access viewmodel using factory which handled and configured with custom Hilt module, either ```DefaultViewModelFactory``` or ```TestViewModelFactory```

### Tested file:

UI package
 - ```ListFragmentTest``` 
 - ```MainFragmentTest``` 
 - ```MainActivityTest``` 
 - ```DetailActivityTest``` [bug,ignored]
 

Data
 - ```RepositoryTest```
 - ```NotificationDatabaseTest```
 - ```RemoteSourceTest```

UnitTest ViewModel

- ```DetailViewModelTest```
- ```DefaultMainViewModelTest```

More detailed can be found in the code.

### Single Author

[Dani Zakaria](https://github.com/danizakaria63)

## License

``` text
MIT License

Copyright (c) 2021 Othneil Drew

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```