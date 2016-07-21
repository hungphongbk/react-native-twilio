import Config from 'react-native-config'

// const Config = {
// 	API_URL: 'http://localhost:8000/',
// }

export default {
    calls: {
        make:  Config.API_URL + 'calls/',
        token: Config.API_URL + 'token/',
    }
}
