# Lesson 8 Content Providers
1. Content Provider 이용하는 이점
    - Easily change underlying data source
    - Leverage functionality of Android Classes
    - Allow many apps to access, use and modify a single datasource securely
2. 다른 앱의 데이터를 이용하기 위해서는 그 앱의 패키지의 permission이 필요하다
    - 예) <uses-permission android:name="com.example.udacity.droidtermsexampple.TERMS_READ"/>
3. Content Provider를 이용하기 위한 단계
    1. Get Permission to use the Content Provider
    2. Get the ContentResolver
    3. Pick one of four basic actions: query, insert, update, delete
    4. Use a URI to identify the data you are reading or manipulating
    5. In the case of reading from the ContentProvider, display the information in the UI
4. ContentResolver 가져오기
~~~
ContentResolver resolver = getContentResolver();
Corsor cursor = resolver.query(
    DroidTermsExampleContract.CONTENT_URI, null, null, null, null);
~~~
5. ContentResolver는 여러 앱들의 요청들을 다루고 동기화를 유지시킨다.
6. ContentProvider에서 할 수 잇는 네가지 기본
    1. Read - query()
    2. add - insert()
    3. update - update()
    4. delete - delete();
7. 정보를 찾기 위해서 Uri(Uniform Resource Identifier) 를 사용한다.
~~~
    Uri = "content://com.example.udacity.droidtermsexample/terms"

    ContentProviderPrefix       ContentAuthority        SpecificData
~~~
droidtermsexampleContract나 CalenderContract등 미리 CONTENT_URI속성을 만들어 둔다.
8. 데이터 베이스 사용을 위해 AsyncTask를 이용해야 한다. <Void, Void, Cursor>로 생성하고
doInBackground에서 Provider를 찾고 커서를 return하여 onPostExecute에서 데이터를 옮겨온다.
9. .query()로 찾을 때 필터 적용 하는 방법
~~~
    resolver.query(Contract.CONTENT_URI,
    <projection>, <selection>, <selection args>, <sort order>);
    projection = filters columns
    selection - statement for how to filter rows
    selection arguments = what to filter
    sort order = how to sort rows
~~~
10. Cursor 사용법들

~~~
    - moveToNext()
        다음 row로 이동, 만약 마지막이면 false return
    - moveToFirst()
        처음 row로 이동
    - getColumnIndex(String heading)
        해당 String이 몇번째 Column인지
    - get<Type>(int ColumnIndex) ex) getString getInt
        해당 column값을 가져온다.
    - getCount()
        커서의 총 row개수 return
    - close()
        메모리 누수를 막기위해 꼭 닫아주기
~~~
