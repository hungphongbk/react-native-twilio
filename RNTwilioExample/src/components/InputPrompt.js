import React, {PropTypes} from 'react'
import {
    View,
    StyleSheet,
    Text,
    TextInput,
} from 'react-native'

class InputPrompt extends React.Component{
    propTypes: {
        value:        PropTypes.string,
        label:        PropTypes.string.isRequired,
        placeholder:  PropTypes.string.isRequired,
        autoCorrect:  PropTypes.bool.isRequired,
        autoFocus:    PropTypes.bool.isRequired,
        keyboardType: PropTypes.string.isRequired,
        onSubmit:     PropTypes.func.isRequired,
        onChangeText: PropTypes.func,
    }
    constructor(props){
        super(props)
    }
    render(){
        return (
            <View>
                <Text>{this.props.label}</Text>
                <TextInput
                    style={styles.input}
                    onChangeText={this.props.onChangeText}
                    onSubmitEditing={this.props.onSubmit}
                    placeholder={this.props.placeholder}
                    value={this.props.value}
                    autoCorrect={this.props.autoCorrect}
                    autoFocus={this.props.autoFocus}
                    keyboardType={this.props.keyboardType}
                />
            </View>
        )
    }
}

const styles = StyleSheet.create({
    input: {
        height: 40,
        borderColor: 'gray',
        borderWidth: 1,
    },
})

module.exports = InputPrompt
