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
<p><a href="group_list.php">Return to Group List</a></p>

{FORM_ERROR}

<form class="defaultform" action="{ACTION}" method="post" id="admin_form" onsubmit="return(validate(this));">
	<fieldset>
		<legend>{LEGEND}</legend>
		
		<input name="id" type="hidden" value="{ID}" />
		
		<!-- Row -->
		
		<div class="column_single">
			<label>Group Name<br />
			<span class="note">Enter a name for this user group.</span><br />
			<input class="single" name="group_name" type="text" value="{GROUP_NAME}" autocomplete="OFF" /></label>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<div class="column_single">
			<p>
				<label for="comments">Comments </label>
				<select name="comments">
					{COMMENT_DROPDOWN}
				</select>
			</p>
			<p><span class="note">All groups should generally be allowed to View and Post comments. Only Authors and Administrators should be able to Moderate, Edit and Delete comments. (You may wish to disable Posting for Guest users.)</span></p>
		</div>
				
		<br clear="left" />
		
		<!-- Row -->
				
		<div class="column_single">
			<p>
				<label for="entries">Blog Entries </label>
				<select name="entries">
					{ENTRY_DROPDOWN}
				</select>
			</p>
			<p><span class="note">Authors are considered the Administrator of their own blog entries. As such, they can always View, Edit and Delete their own posts and the comments attached to them. These options relate to <em>all authors</em> blog entries.</span></p>
			<p><span class="note">Only Authors and Administrators should be able to Post, Edit and Delete entries. (If you wish to have a "private" blog, you can disable Viewing for Guest users.)</span></p>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
				
		<div class="column_single">
			<p><label><input name="superuser" type="checkbox"{SUPERUSER_CHECKED} /> Administrator</label></p>
			<p><span class="note">Administrators are considered super-users and have full access to all parts of the blog. Including themes, settings, user and group permissions, etc...</span></p>
		</div>
		
		<br clear="left" />
		
		<!-- Row -->
		
		<input name="submit" type="submit" value="{COMMAND}" />
	</fieldset>
</form>
