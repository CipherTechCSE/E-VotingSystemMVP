import requests
import json
import random
import string

class ElectionSystem:
    def __init__(self, base_url):
        self.base_url = base_url  # Base URL of the Spring application
        self.token = None  # To store the Bearer token for admin access
        self.group_encryption_parameters = None  # To store the group encryption parameters

    def login(self, username, password):
        # Send POST request to the login endpoint
        response = requests.post(f"{self.base_url}/auth/login", json={"username": username, "password": password})
        if response.status_code == 200:
            self.token = response.json().get("token")  # Assuming the token is returned in the response JSON
            print("Admin logged in successfully.")
            return True
        else:
            print("Login failed:", response.json())
            return False
        
    def logout(self):
        # Send POST request to the logout endpoint
        headers = {"Authorization": f"Bearer {self.token}"}
        response = requests.post(f"{self.base_url}/auth/logout", headers=headers)
        if response.status_code == 200:
            print("Admin logged out successfully.")
            self.token = None  # Clear the token
        else:
            print("Logout failed:", response.json())

    def create_election(self, election_name, start_date, end_date, is_active):
        # Send POST request to create an election
        payload = {
            "electionName": election_name,
            "startDate": start_date,
            "endDate": end_date,
            "isActive": is_active
        }
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/authority/admin/election", json=payload, headers=headers)
        if response.status_code == 201:
            election_id = response.json().get('data').get('id')  # Assuming the election ID is returned in the response JSON
            print(f"Election created successfully with ID: {election_id}")
            return election_id
        else:
            print("Error creating election:", response.json())
            return None
        
    def add_candidate(self, name, party, nic):
        # Send POST request to add candidates to the election
        payload = {
            "nic": nic,
            "name": name,
            "party": party
        }
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/authority/admin/candidate", json=payload, headers=headers)
        if response.status_code == 201:
            print("Candidates added successfully.")
        else:
            print("Error adding candidates:", response.json())

    def start_election(self, election_id):
        # Send POST request to start the election
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/admin/start-election/{election_id}", headers=headers)
        if response.status_code == 200:
            print(f"Election {election_id} started.")
        else:
            print("Error starting election:", response.json())

    def end_election(self, election_id):
        # Send POST request to end the election
        headers = {"Authorization": f"Bearer {self.token}"}
        response = requests.post(f"{self.base_url}/admin/end-election/{election_id}", headers=headers)
        if response.status_code == 200:
            print(f"Election {election_id} ended.")
        else:
            print("Error ending election:", response.json())

    def register_user(self, username, password, device_fingerprint, email, phone_number, full_name, address, nic):
        # Send POST request to register a user with additional details
        payload = {
            "username": username,
            "password": password,
            "deviceFingerprint": device_fingerprint,
            "email": email,
            "phoneNumber": phone_number,
            "fullName": full_name,
            "address": address,
            "nic": nic
        }
        response = requests.post(f"{self.base_url}/auth/register", json=payload)
        if response.status_code == 200:
            print(f"User {username} registered successfully.")
        else:
            print("Error registering user:", response)

    def register_admin(self, username, password, device_fingerprint, email, phone_number, full_name, address, nic):
        
        # Send POST request to register an admin with additional details
        payload = {
            "accessToken": "24327492384798234",
            "username": username,
            "password": password,
            "deviceFingerprint": device_fingerprint,
            "email": email,
            "phoneNumber": phone_number,
            "fullName": full_name,
            "address": address,
            "nic": nic
        }
        response = requests.post(f"{self.base_url}/auth/register-admin", json=payload)
        if response.status_code == 200:
            print(f"Admin {username} registered successfully.")
        else:
            print("Error registering admin:", response)

    def get_group_encryption_parameters(self, election_id):
        # Send GET request to get group encryption parameters
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.get(f"{self.base_url}/authority/group-encryption-parameters/{election_id}", headers=headers)
        if response.status_code == 200:
            self.group_encryption_parameters = response.json()['data']  # Store the parameters
            return self.group_encryption_parameters
        else:
            print("Error getting group parameters:", response.json())
            return None

    def join_group(self, username, random_number):
        # Send POST request for user to join group
        payload = {"username": username, "random_number": random_number}
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/user/joinGroup", json=payload, headers=headers)
        if response.status_code == 200:
            print(f"User {username} joined the group with key: {random_number}")
        else:
            print("Error joining group:", response.json())

    def request_ballot(self, username, election_id):
        # Send GET request to request a ballot
        payload = {"username": username, "election_id": election_id}
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.get(f"{self.base_url}/user/requestBallot", json=payload, headers=headers)
        if response.status_code == 200:
            return response.json()  # Return the ballot info
        else:
            print("Error requesting ballot:", response.json())
            return None

    def vote(self, username, ballot_id, candidate):
        # Send POST request to vote
        payload = {
            "username": username,
            "ballot_id": ballot_id,
            "candidate": candidate
        }
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/user/vote", json=payload, headers=headers)
        if response.status_code == 200:
            return response.json()  # Return vote info
        else:
            print("Error voting:", response.json())
            return None

    def blind_vote(self, vote):
        # Simple blinding (dummy operation for demonstration)
        blinded_vote = json.dumps(vote)[::-1]  # Reverse the vote string as a form of blinding
        print(f"Blinded vote: {blinded_vote}")
        return blinded_vote

    def sign_vote(self, blinded_vote):
        # Sign the blinded vote using the authority's private key
        response = requests.post(f"{self.base_url}/authority/signVote", json={"blinded_vote": blinded_vote})
        if response.status_code == 200:
            print("Authority signed the blinded vote.")
            return response.json()["signature"]  # Assuming the signature is returned
        else:
            print("Error signing vote:", response.json())
            return None

    def unblind_vote(self, blinded_vote):
        # Send POST request to unblind the vote
        response = requests.post(f"{self.base_url}/user/unblindVote", json={"blinded_vote": blinded_vote})
        if response.status_code == 200:
            unblinded_vote = response.json()["unblinded_vote"]  # Assuming the unblinded vote is returned
            print(f"Unblinded vote: {unblinded_vote}")
            return unblinded_vote
        else:
            print("Error unblinding vote:", response.json())
            return None

    def submit_ballot(self, username, ballot, signature):
        # Send POST request to submit the ballot
        payload = {
            "username": username,
            "ballot": ballot,
            "signature": signature
        }
        headers = {"Authorization": f"Bearer {self.token}"}  # Include the Bearer token
        response = requests.post(f"{self.base_url}/user/submitBallot", json=payload, headers=headers)
        if response.status_code == 200:
            print(f"User {username} submitted the ballot successfully.")
        else:
            print("Error submitting ballot:", response.json())

# Simulation of the process
if __name__ == "__main__":
    base_url = "http://localhost:8080/api"  # Update with your Spring application URL
    election_system = ElectionSystem(base_url)

    created_election_id = None

    # create admin
    election_system.register_admin("admin", "Abc@1234", "device6464", "admin@gmail.com", "1234567890", "John Doe", "123 Main St", "522256785V")

    # Step 1: Admin Login
    if election_system.login("admin", "Abc@1234"):
        # Step 2: Create Election
        created_election_id = election_system.create_election("election_2024", "2024-10-27T08:55:56.065Z", "2024-10-28T08:55:56.065Z", False)

        election_system.add_candidate("Candidate A", "Party 1", "123456789V")
        election_system.add_candidate("Candidate B", "Party 2", "987654321V")
        election_system.add_candidate("Candidate C", "Party 3", "246813579V")
        election_system.add_candidate("Candidate D", "Party 4", "135792468V")

        election_system.logout()  # Admin logs out after setting up the election


    # Step 4: User Self-Registration
    election_system.register_user("user1", "password1", "device123", "user1@gmail.com", "1234167710", "John Doe", "123 Main St", "123456789V")
    election_system.register_user("user2", "password2", "device456", "user2@gmail.com", "0987654321", "Jane Smith", "456 Elm St", "987654321V")
    election_system.register_user("user3", "password3", "device789", "user3@gmail.com", "1357924680", "Alice Wonderland", "789 Oak St", "246813579V")

    # Step 5: User Login to Get Token
    # if election_system.login("user1", "password1"):
    #     # Step 6: Get Group Encryption Parameters
    #     election_system.get_group_encryption_parameters(created_election_id)
    #     print(f"Group Encryption Parameters: {election_system.group_encryption_parameters}")

    #     # Step 7: Join Group
    #     election_system.join_group("user1", "random123")

    #     # Step 8: Request Ballot
    #     ballot = election_system.request_ballot("user1", "election_2024")

    #     # Step 9: Vote and Blind
    #     vote = election_system.vote("user1", ballot["ballot_id"], "Candidate A")
    #     blinded_vote = election_system.blind_vote(vote)

    #     # Step 10: Authority Signing
    #     signature = election_system.sign_vote(blinded_vote)

    #     # Step 11: Unblind and Verify
    #     unblinded_vote = election_system.unblind_vote(blinded_vote)

    #     # Step 12: Submit Ballot
    #     election_system.submit_ballot("user1", unblinded_vote, signature)

