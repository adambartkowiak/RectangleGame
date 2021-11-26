package pl.adambartkowiak.support.opengl.gltf

data class GlTF(
    val asset: Asset?,
    val scene: Int?,
    val scenes: List<Scene>,
    val nodes: List<Node>,
    val meshes: List<Mesh>,
    val accessors: List<Accessor>,
    val bufferViews: List<BufferView>,
    val buffers: List<Buffer>,
    val skins: List<Skin>?,

    val materials: List<Material>?,
    val textures: List<Source>?,
    val images: List<Image>?
)