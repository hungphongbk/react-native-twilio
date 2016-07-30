'use strict'

import React, {PropTypes} from 'react'
import {
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native'
import { connect } from 'react-redux'
import {bindActionCreators} from 'redux'

import {
    initTelephonyStart,
    makeCallStart,
} from '../actions'

import InputPrompt from '../components/InputPrompt'

const AppContainer = React.createClass({

    renderTelephonyStatus() {
        if (this.props.deviceError) {
            return <Text>Telephone error {this.props.deviceError}</Text>
        }
        if (this.props.deviceReady) {
            return <View>
                <Text>Telephone READY</Text>
                <Text>Listening {this.props.listening}</Text>
            </View>
        }
        if (this.props.clientName) {
            return <View>
                <Text>Telephone NOT READY</Text>
                <TouchableOpacity
                    style={styles.button}
                    onPress={() => {this.props.telephonyActions.initTelephonyStart(this.props.clientName)}}>
                    <Text style={styles.buttonText}>init device with token</Text>
                </TouchableOpacity>
            </View>
        }
    },

    // componentDidMount() {
    //     this.props.telephonyActions.initTelephonyStart(this.props.clientName)
    // },

    getInitialState() {
        return {
            callToNumber: this.props.callToNumber
        }
    },

    renderInputNumber() {
        if (this.props.deviceReady === false) {
            return null
        }

        // this.state.callToNumber = this.props.callToNumber
        this.state.callToNumber = '+447423490383'

        let callNumber = () => {
            this.props.telephonyActions.makeCallStart(this.state.callToNumber)
        }
        let onSubmit = (event) => {
            this.state.callToNumber = event.nativeEvent.text
            callNumber()
        }
        return <View>
            <InputPrompt
                label="phone number to call"
                placeholder="Who do you want to call?"
                value={this.state.callToNumber}
                autoCorrect={false}
                autoFocus={false}
                keyboardType="phone-pad"
                onSubmit={onSubmit}
                />
                <TouchableOpacity
                    style={styles.button}
                    onPress={() => {callNumber()}}>
                    <Text style={styles.buttonText}>DIAL</Text>
                </TouchableOpacity>
            </View>
    },

    renderCallProgress() {
        if (this.props.deviceReady === false) {
            return null
        }

        if (this.props.callStatus === 'out') {
            return <View>
                <Text>You are dialing {this.props.callToNumber}</Text>
            </View>
        }
        if (this.props.callStatus === 'in') {
            return <View>
                <Text>{this.props.callFromNumber} is calling</Text>
            </View>
        }
    },

    renderCallingError() {
        if (this.props.callingError) {
            return <Text>calling errors: {this.props.callingError}</Text>
        }
    },

    render() {

        return (
            <View style={styles.outerContainer}>
                <Text>React Native Twilio Example</Text>
                {this.renderTelephonyStatus()}
                <Text>Your client name is: {this.props.clientName}</Text>
                {this.renderInputNumber()}
                {this.renderCallProgress()}
                {this.renderCallingError()}
            </View>
        )
    }

})


AppContainer.propTypes = {
    telephonyActions: PropTypes.object.isRequired,
    clientName:       PropTypes.string.isRequired,
    callToNumber:     PropTypes.string,
    callFromNumber:   PropTypes.string,
    callingError:     PropTypes.string,
    callStatus:       PropTypes.string,
    listening:        PropTypes.bool.isRequired,
}

const styles = StyleSheet.create({
    outerContainer: {
        flex: 1,
        padding: 10,
    },
    container: {
        flex: 1,
    },
    button: {
        height: 45,
        flexDirection: 'row',
        backgroundColor: 'white',
        borderColor: '#111',
        borderWidth: 1,
        borderRadius: 5,
        marginBottom: 5,
        marginTop: 5,
        alignSelf: 'stretch',
        justifyContent: 'center'
    },
    buttonText: {
        fontSize: 18,
        color: '#111',
        alignSelf: 'center',
    },
})

function mapStateToProps(state) {
    const {
        telephonyState,
        callState,
    } = state
    const {
        initTelephony,
        deviceReady,
        clientName,
    } = telephonyState

    const {
        callToNumber,
        callFromNumber,
        callingError,
        callStatus,
        listening,
    } = callState
    return {
        initTelephony,
        deviceReady,
        clientName,
        callToNumber,
        callFromNumber,
        callingError,
        callStatus,
        listening,
    }
}

export default connect(
    mapStateToProps,
    dispatch => ({
        telephonyActions: bindActionCreators({initTelephonyStart, makeCallStart}, dispatch),
    })
)(AppContainer)
