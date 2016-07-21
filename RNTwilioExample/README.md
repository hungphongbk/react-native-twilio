# react-native-twilio example project

ios is not done

to start

You need to make the Twilio Python server run

`https://github.com/twilio/mobile-quickstart`

```bash
npm install
ENVFILE=.env react-native run-android
sudo ./adb reverse tcp:8081 tcp:8081
npm start
```

