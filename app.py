import streamlit as st
import requests
import json


st.set_page_config(page_title="Smart App Review Responder", layout="wide")

# Initialize session state variables if they don't exist
if "messages" not in st.session_state:
    st.session_state.messages = [
        {"role": "assistant", "content": "Hi there! I'm your review assistant. Share your experience with our app and I'll provide a helpful response. How was your experience?"}
    ]

if "rating" not in st.session_state:
    st.session_state.rating = 3

# Title with some styling
st.markdown("<h1 style='text-align: center;'>Smart App Review Responder</h1>", unsafe_allow_html=True)

# Create a container for the chat interface
chat_container = st.container()

# Create a sidebar for rating
with st.sidebar:
    st.subheader("Rate your experience")
    st.session_state.rating = st.slider(
        label="App Rating",
        min_value=1,
        max_value=5,
        value=st.session_state.rating,
        help="Select a rating from 1 to 5 stars"
    )
    
    st.markdown("---")
    st.markdown("### About")
    st.markdown("This chatbot helps collect and respond to app reviews. Share your thoughts and get personalized responses!")

# Display chat messages
with chat_container:
    for message in st.session_state.messages:
        with st.chat_message(message["role"]):
            st.write(message["content"])

    # User input
    user_input = st.chat_input("Type your review here...")
    
    if user_input:
        # Add user message to chat history
        st.session_state.messages.append({"role": "user", "content": user_input})
        
        # Display user message immediately
        with st.chat_message("user"):
            st.write(user_input)
        
        # Process user input
        with st.chat_message("assistant"):
            with st.spinner("Thinking..."):
                try:
                    # API endpoint
                    api_url = "http://localhost:8081/api/reviews/respond"
                    
                    # Prepare payload
                    payload = {
                        "reviewText": user_input,
                        "rating": st.session_state.rating
                    }
                    
                    # Make API request
                    response = requests.post(api_url, json=payload)
                    api_response = response.json()
                    
                    # Extract response text and sentiment
                    response_text = api_response.get("responseText", "Thank you for your feedback!")
                    sentiment = api_response.get("sentiment", "NEUTRAL")
                    
                    # Display the response text
                    st.write(response_text)
                    
                    # Display sentiment indicator (small and subtle)
                    if sentiment == "POSITIVE":
                        st.markdown("<span style='color:green; font-size:0.8em;'>✓ Positive sentiment detected</span>", unsafe_allow_html=True)
                    elif sentiment == "NEGATIVE":
                        st.markdown("<span style='color:red; font-size:0.8em;'>! Negative sentiment detected</span>", unsafe_allow_html=True)
                    
                    # Add assistant response to chat history
                    st.session_state.messages.append({"role": "assistant", "content": response_text})
                    
                except Exception as e:
                    error_message = f"Sorry, I couldn't process your request. Error: {str(e)}"
                    st.error(error_message)
                    st.session_state.messages.append({"role": "assistant", "content": error_message})

# Add a reset button at the bottom
if st.button("Reset Conversation"):
    st.session_state.messages = [
        {"role": "assistant", "content": "Hi there! I'm your review assistant. Share your experience with our app and I'll provide a helpful response. How was your experience?"}
    ]
    st.session_state.rating = 3
    st.rerun()