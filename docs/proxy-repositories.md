# Proxy repositories

The project uses [Space Packages](https://jetbrains.team/p/krpc/packages) to proxy all its dependencies on CI runs.
By default, repositories are NOT proxied locally. 
That means that external contributors can easily modify the project. 
However, all dependency changes MUST be reviewed before running CI. 

# JetBrains developers
For JetBrains developers, there is a possibility to use proxy repositories locally.
It is actually encouraged to do so.

For it to work, add the following properties to your `$HOME/.gradle/gradle.properties` file 
(or `local.properties` in the repository root):
```properties
kotlinx.rpc.team.space.username=<username>
kotlinx.rpc.team.space.password=<password>
kotlinx.rpc.useProxyRepositories=true
```

In Space your username can be found in your profile, it is `Name.Surname`.

Password can be generated on the [packages](https://jetbrains.team/p/krpc/packages/maven/build-deps) page:
- Press `Connect` in the top right corner
- Change `Generate Read Token` to `Generate Write Token` using dropdown.
- Press `Generate Write Token`
- Press `Copy write token`
- Past the value into the property field
