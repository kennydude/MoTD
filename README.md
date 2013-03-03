# March of the Droids

Android Application

Uses Maven.

Test builds can be done:

	mvn install; adb -r target/*.apk

(Manually using adb speeds things up a lot).

Release builds require a settings.xml that is based of this (you **must** put this file in ~/.m2/settings.xml or it won't work. Non-Linux users should figure out themselves where that should be):

	<settings>
		<profiles>
			<profile>
				<activation>
					<activeByDefault>true</activeByDefault>
				</activation>
				<properties>
					<sign.keystore>/absolute/path/to/your.keystore</sign.keystore>
					<sign.alias>youralias</sign.alias>
					<sign.keypass>keypass</sign.keypass>
					<sign.storepass>storepass</sign.storepass>
				</properties>
			</profile>
		</profiles>
	</settings>


Then

	mvn -Prelease install
