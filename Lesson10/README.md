# Lesson10  Background Tasks
1. Services는 manifest에 기입해야하는 네가지 중 하나이다. 엑티비티가 작동하지 않아도 작동하며 푸쉬알림등 이 그 예이다.
2. Services VS Loader
    1. Loader
        - Tied to the Activity LifeCycle
        - Easy to make user interface changes and communicate with Activity
        - If loading or processing data that will be used in the UI
    2. Services
        - Decoupled from the user interface
        - Exists even when there is no user interface
        - If need to process upload or download data in a way where the end result will not directly affect the UI
3. Service 시작하기
    - Start : startService()를 context (예를들어 activity)에서 실행 하지만 시작한 곳으로 후에 돌아오지 않는다.
    - Scheduling : JobService - scheduler(예를들어 JobScheduler or Firebase JonDispatcher)를 이용하여 미래 어느시점이나 이벤트가 발생하면 실행되게 한다.
    - bind : Client Server같이 service가 server가 되고 service에 binding된 것들이 clients가 된다. bindService 를 이용해 바인딩된다. start와 다르게 서비스와 소통한다. 음악 플레이어 생각하면 음악 바뀌면 ui 바뀌고 재생 버튼 누르면 노래 재생되는거 생각하자.
4. StartService LifeCycle
    - call to StartService()
    - onCreate()
    - onStartCommand()
    - Service Running //Start the Async Task here.
    - stopSelf() //stopped by itself or a clients
    - onDestroy()
    - Services Shut Down
5. Intent를 이용한 StartService, activity와 유사하게 자료들을 intent에 담아 보내고 꺼내 쓸 수 있다.
    - 만들기 -> 상속, HandleIntent override -> startService
~~~
    public class MyIntentService extends IntentService{//IntentService 상속
        @Override
        protected void on HandleIntent(Intent intent){
            //do In Background
        }
    }


    Intent myIntent = new Intent(this, MyintentService.class);
    startService(myIntent);
~~~
    - 매니페스트 등록
~~~
    <service
        android:name=".sync.WaterReminderIntentService" //.sync. + 서비스class이름
        android:exported="false"/>
~~~
    - 세부분으로 나뉜다. IntentService 상속받은 ServiceClass, ServiceClass의 HandleIntent에서 실행할 TaskClass, ServiceClass를 가져와서 startActivity할 AvtivityClass
6. PendingIntent
    - 직접 intent를 보내지 않고 다른 activity에 intent를 넘겨주는것.
~~~
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivities(context, WATER_REMINDER_PENDING_INTENT_ID, intent,PendingIntent.FLAG_UPDATE_CURRENT);//id는 고유값, FLAG는 새로 만들지 않고 전에 있던 intent를 업데이트 한다는 뜻
~~~
7. notification 만들기 설명 생략 되게 복잡하다... 외우진 못할듯
~~~
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)//manifest permission 등록해야한다.
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, builder.build());
~~~
8. clearNotification
~~~
    public static void clearNotification(Context context){//notification의 버튼을 누르거나 할일을 한 뒤 꼭 clear해 주어야 한다.
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
~~~
9. notification action 추가하기
    - action을 만들어 return하는 함수를 만든다.
~~~
    public static NotificationCompat.Action ignoreReminderAction(Context context){
        Intent intent = new Intent(context, WaterReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(context, DISMISS_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_cancel_black_24px, "No Thanks", pendingIntent);
        return ignoreReminderAction;
    }
~~~
    - 위의 NotificationCompat.Builder 로 빌더만들때 .setAction(ignoreReminderAction(context)) 를 끼워준다.
10. foreGround Service는 유저가 계속 notification 이 필요할 때
11. Application Priority
    - Critical : foreground Activity > foreground Services : 무거운 foreground Process나 foreground Service를 실행하면 다른 foreground Service를 kill 할 수 있다.
    - High : visible activities : 사용자가 알 수 잇기때문에 kill하는것은 바람직하지 않다.
    - Medium : Service Processes : kill 될 수 있다.
    - Low : Background Processes, Empty Processes : Empty가 가장 빨리 kill 된다.
12. Three laws of Android Resource Management
    1. Android will keep all apps that interact with the user running smoothly.
    2. Android will keep all apps with visible activities followed by services running, unless doing so violates the first laws.
    3. Android will keep all apps in the background running, unless this violates the first or second law.

## Chapter 4
13. Add FirebaseJobDispatcher
    1. Add the Gradle dependency
        - compile 'com.firebase:firebase-jobdispatcher:0.5.0'
    2. Great a new task in our Reminder Tasks
    3. Create anew Service that extends from JobService
        - JobService 를 상속받는 클래스 생성, onStartJob(final JobParameters params) 에서 AsyncTask()생성하고
        - dpInBackground 에서 일 실행, onPostExecute에서 jobFinished 실행.
        - onStopJob에서는 만들었던 AsyncTask가 유요한지 확인하고 만약 유요하다면 cancel(true)시켜준다.
    4. Add the jobService to the manifest : 다음과 같이 추가한ㄷ. intent-filter는 이 service를 시행하기 위해 쓸 intent종류를 의미한다.
    ~~~
        <service android:name=".sync.WaterReminderFirebaseJobService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.firebase.jobdispatcher.ACTION_EXECUTE"/>
            </intent-filter>
        </service>
    ~~~
    5. Schedule with FirebaseJobDispatcher
        - ReminderUtilities라는 class 파일을 새로 만들어 다음과같은 코드를 입력했다. 굳이 하나 더 만들 필요는 없는것 같다.
    ~~~
        public static void scheduleChargingReminder(Context context){
            if(sInitialized == true) return;
            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher= new FirebaseJobDispatcher(driver);

            Job reminderJob = dispatcher.newJobBuilder()
                    .setService(WaterReminderFirebaseJobService.class)
                    .setTag(REMINDER_JOB_TAG)
                    .setConstraints(Constraint.DEVICE_CHARGING)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))//몇초부터 몇초 사이에 실행해 주세요 하는 코드
                    .setReplaceCurrent(true)
                    .build();
            dispatcher.schedule(reminderJob); //dispatcher 생성 후 스케줄하기
        }        
    ~~~

14. BroadCast Receivers
    - 어플리케이션이 다른 어플리케이션이나 시스템으로부터 BroadCast된 intent를 받게 해주는 요소
    - 다른 어플리케이션이 실행되지 않아도 실행 가능하다.
    - static BroadCast Receivers : 요청이 올 때마다 실행되고 어플리케이션이 오프라인이어도 실행된다.
    - dynamic BroadCast Receivers : 어플리케이션 lifeCycle과 함께한다.
    - 보통은 dynamic Broadcast Receivers 나 jobScheling을 쓰는게 낫다.
15. dynamic Broadcast Receiver 만들기 // 꽤나 쉽다
    - IntentFilter, BroadcastReceiver 를 클래스 변수로 선언하자.
    - onCreate에서 intentFilter를 생성하고 저장한다. 그리고 어떤 요청을 받을 것인지 addAction으로 넣어준다. intentFilter.addAction(Intent.ACTION_POWER_CONNECTED); // ACTION_POWER_CONNECTED는 충전중일때.
    - BroadcastReceiver를 상속받는 클래스를 만든다. onReceive를 override하고 여기에 요청이 날라오면 무엇을 할 것인지 기술한다.
    - onCreate에서 위에만든 BroadcastReceiver를 생성하고 변수에 저장한다.
    - onResume 과 onPause에 각각 registerReceiver(BroadcastReceiver, IntentFilter), unregisterReceiver(BroadcastReceiver, IntentFilter); 를 실행 해 준다.
