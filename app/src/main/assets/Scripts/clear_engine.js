if (globalThis.scene) {
    scene.dispose();
    engine.dispose();
    scene = null;
    engine = null;
}