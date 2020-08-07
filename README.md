# INFS3605 - H13A - Group 3 Mudskipper

Bugs/Isses to look out for:
1. This app has run successfully on API 25, 29 and 30. However, first time installation from android studio can sometimes crash
Retrying should be successfull

1. Due to an unknown reason, sometimes when the project is imported or cloned, the folders might not appear in the project view
but can be seen in the project view


2. Due to merging issues, signing in with google works, but there seems to be a delay between when the method is called and when the data is
returned from firebase, which leads to calling the else statement in the following code snippet as the userList array is 0 so will the if
statement is always skipped.ONLY NSW AND VIC STATES HAVE WORKING CITIES LIST AS A DEMO ON THE FUNCTION TO ENABLE THE DEVELOPERS TO WORK ON OTHER FUNCTIONS.


"""

                            for (String email:userList){
                                Log.e(TAG, "in email loop" + email + "  " + user.getEmail());
                                if (email.equals(user.getEmail())){
                                    Log.e(TAG, "equal");
                                    isRegistered =true;
                                }
                            }
                            if (isRegistered==true){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else{
                                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                intent.putExtra("google_userID",mAuth.getUid());
                                System.out.println(mAuth.getUid());
                                startActivity(intent);
                            }

"""

3. After clicking register, the user might need to wait a few seconds before the upload to the database is successful and the next activity is started
4. Due to a bug in the multiselectSpinner library used, the hint text does not appear, only an arrow on the right of the spinner, but the spinner works fine
5. Due to time constraints, the edit profile page is not updated to reflect the additional data collected on registration process
6. The only method currently to access the messging feature at the moment is through the message button on the current user's profile page
7. When the use has updated their data in edit profile, they are redirected back to their profile page but resetting the fragment does not work,
However, if the user clicks on the discover/project tab then back to the profile tab, the fragment is reset and new data is shown. The same goes after the user has
added a new project
8. The notifications folder contains fils for notification from messaging but does not work and was deprioritised
9. Current prjects can only be liked in their detail page and will increase everytime it is tapped
10. There is currently no limit as to how many skills and occupation can each user select on registration
11.Search function in the messaging function crashes and the user has to click the back button on their phone to go back to the other pages from the messaging feature







Reference used for messaging function and related files: https://www.youtube.com/playlist?list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8
This tutorial enabled me to implement a basic messaging feature in our application to enourage interactions and collaboration between user.

