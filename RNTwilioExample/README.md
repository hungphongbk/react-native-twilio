# react-native-twilio example project

# ios
Currently the ios example app is not done.

# android

## setup

### server

Download and run a simple server with your Twilio credentials.
For example you can use:
[mobile-quickstart](https://github.com/twilio/mobile-quickstart)

You will need to setup an TwiML app through the Twilio Console and use the `APP_ID` for the server configuration.

You will need to expose the server to the outside so that Twilio can contact it when it looks for the TwiML to answer calls. You can use [ngrok](https://ngrok.com/) to do that

	./ngrok http 5000

### react-native
You will need to have `react-native` installed and available in your `PATH`.

Add your server IP address to `.env`

	ENV=dev
	API_URL=http://SERVER_IP_ADDRESS:YOUR_SERVER_PORT/

```bash
# install JS dependencies
cd RNTwilioExample
npm install

# ensure the connection to the JS packager
./adb reverse tcp:8081 tcp:8081

# run the JS packager
npm start

# compile and install a debug build
ENVFILE=.env react-native run-android

# this should be it!
```

If you find problems with this instructions or want to make them more clear open an issue or PR respectively.

