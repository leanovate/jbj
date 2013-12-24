<!-- VALIDATION -->
<script type="text/javascript">
	// <!--
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g,"");
	}
	
	function validate(theform) {
		return true;
		
		theform.user_name.value = theform.user_name.value.trim();
		theform.password.value = theform.password.value.trim();
		theform.display_name.value = theform.display_name.value.trim();
		theform.email.value = theform.email.value.trim();
		theform.avatar.value = theform.email.avatar.trim();
		
		// Required Fields
		if (theform.user_name.value=="") {
			alert("Please enter a user name");
			theform.user_name.focus();
			return false;
		}
		if (theform.password.value=="") {
			alert("Please enter a password");
			theform.password.focus();
			return false;
		}
		if (theform.display_name.value=="") {
			alert("Please enter a display name");
			theform.display_name.focus();
			return false;
		}
		
		return true;
	}
	// -->
</script>

<!-- FORM -->
<p class="instructions">{INSTRUCTIONS}</p>
<p><a href="user_list.php">Return to User List</a></p>

{FORM_ERROR}

<form class="defaultform" action="{ACTION}" method="post" id="admin_form" onsubmit="return(validate(this));">
	<fieldset>
		<legend>{LEGEND}</legend>
		
		<input name="id" type="hidden" value="{ID}" />
		
		<!-- Row -->
		
		<div class="column_double">
			<label>User Name<br />
			<span class="note">Your login name.</span><br />
			<input class="double" name="user_name" type="text" value="{USER_NAME}" autocomplete="OFF" /></label>
		</div>
		
		<div class="column_double">
			<label>Password<br />
			<span class="note">Your login password.</span><br />
			<input class="double" name="password" type="password" value="{PASSWORD}" autocomplete="OFF" /></label>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<div class="column_double">
			<label>Display Name<br />
			<span class="note">Name which appears on your posts.</span><br />
			<input class="double" name="display_name" type="text" value="{DISPLAY_NAME}" autocomplete="OFF" /></label>
		</div>
		
		<div class="column_double">
			<label>Email Address<br />
			<span class="note">Email address for notifications.</span><br />
			<input class="double" name="email" type="text" value="{EMAIL}" autocomplete="OFF" /></label>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<div class="column_single">
			<label>Avatar Image<br />
			<span class="note">Enter the URL of your avatar image.</span><br />
			<input class="single" name="avatar" type="text" value="{AVATAR_NAME}" autocomplete="OFF" /></label>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<div class="column_single">
			<label>Group<br />
			<span class="note">Permissions are based on a user's group.</span><br />
			<select name="group">
				{GROUP_DROPDOWN}
			</select>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<div class="column_single">
			<label><input name="active" type="checkbox"{ACTIVE_CHECKED} /> Activate</label><br />
			<span class="note">Enable this account.</span>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<input name="submit" type="submit" value="{COMMAND}" />
	</fieldset>
</form>
