function isRemappingEnabledForClass(node) {
    var flag2 = !node.name.startsWith("net.sushiclient.client.mixin.*");
    return flag2;
}
function isObfuscatorEnabledForClass(node) {
    var flag2 = !node.name.startsWith("net.sushiclient.client.mixin.*");
    return flag2;
}