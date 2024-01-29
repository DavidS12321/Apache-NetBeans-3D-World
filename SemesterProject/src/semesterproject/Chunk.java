package semesterproject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
   
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final float persistanceMin = 0.03f;
    static final float persistanceMax = 0.06f;
    private Random random = new Random();
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX;
    private int StartY;
    private int StartZ;
    private Random r;
    private boolean inverted = false;
    private boolean skin = false;

    private int VBOTextureHandle;
    private Texture texture;
   
   
    public void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3,GL_FLOAT, 0, 0L);
       
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
       
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE *CHUNK_SIZE* CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        float persistance = 0;
        while (persistance < persistanceMin) {
            persistance = persistanceMax*random.nextFloat();
        }
       
        int seed = (int)(50*random.nextFloat());
       
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistance, seed);
       
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);

        float height;
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                // Height randomized
                int i = (int) (startX + x * ((300 - startX) / 640));
                int j = (int) (startZ + z * ((300 - startZ) / 480));
                height = 1+Math.abs((startY + (int)(100*noise.getNoise(i, j))*CUBE_LENGTH));
                persistance = 0;
                
                for (float y = 0; y <= 3; y++) {
                    Block.BlockType blockType;

                    if (y == 0) {
                        blockType = Block.BlockType.BlockType_Bedrock;
                    } else if (y == 1) {
                        if(skin){
                            blockType = Block.BlockType.BlockType_Obsi;
                        }else{
                            blockType = Block.BlockType.BlockType_Stone;
                        }
                    } else if (y == 2) {
                        float randomValue = random.nextFloat();

                        if (randomValue < 0.4f) {
                            if(skin){
                                blockType = Block.BlockType.BlockType_Obsi;
                            }else{
                                blockType = Block.BlockType.BlockType_Stone;
                            }
                        } else if (randomValue < 0.3f) {
                            if(skin){
                                blockType = Block.BlockType.BlockType_Nether;
                            }else{
                                blockType = Block.BlockType.BlockType_Dirt;
                            }
                        } else{
                            if(skin){
                            blockType = Block.BlockType.BlockType_Gold;
                            }else{
                                blockType = Block.BlockType.BlockType_Sand;
                            }
                        }
                    } else {
                        float randomValue = random.nextFloat();

                        if (randomValue < 0.9f) {
                            if(skin){
                                blockType = Block.BlockType.BlockType_Lava;
                            }else{
                                blockType = Block.BlockType.BlockType_Water;
                            }
                        } else {
                            if(skin){
                            blockType = Block.BlockType.BlockType_Gold;
                        }else{
                            blockType = Block.BlockType.BlockType_Sand;
                        }
                        }    
                    }

                    Blocks[(int) x][(int) y][(int) z] = new Block(blockType);
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y*CUBE_LENGTH+ (int)(CHUNK_SIZE*.8)), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks [(int) x] [(int) y] [(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
                
                
                for (float y = 4; y < height; y++) {
                    Block.BlockType blockType;

                    if (y == 4) {                        
                        if(skin){
                             blockType = Block.BlockType.BlockType_Nether;
                        }else{
                             blockType = Block.BlockType.BlockType_Dirt;
                        }
                    } else if (y == 5) {
                        if(skin){
                            blockType = Block.BlockType.BlockType_Gold;
                        }else{
                            blockType = Block.BlockType.BlockType_Sand;
                        }
                    } else {
                        if(skin){
                            blockType = Block.BlockType.BlockType_Nether;
                        }else{
                            blockType = Block.BlockType.BlockType_Grass;
                        }
                    }

                    Blocks[(int) x][(int) y][(int) z] = new Block(blockType);
                    
                    VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float)(y*CUBE_LENGTH+ (int)(CHUNK_SIZE*.8)), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks [(int) x] [(int) y] [(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int)(x)][(int) (y)][(int) (z)]));
                    
                    if (inverted) {
                        switch (blockType) {
                            case BlockType_Bedrock:
                                blockType = Block.BlockType.BlockType_Water;
                                break;
                            case BlockType_Stone:
                                blockType = Block.BlockType.BlockType_Dirt;
                                break;
                            case BlockType_Dirt:
                                blockType = Block.BlockType.BlockType_Stone;
                                break;
                            case BlockType_Sand:
                                blockType = Block.BlockType.BlockType_Grass;
                                break;
                            case BlockType_Grass:
                                blockType = Block.BlockType.BlockType_Sand;
                                break;
                            case BlockType_Water:
                                blockType = Block.BlockType.BlockType_Bedrock;
                                break;
                            // Add cases for other block types if needed
                        }
                    }
                    
                    
                    

                Blocks[(int) x][(int) y][(int) z] = new Block(blockType);
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void toggleNewChunk(boolean inverted) {
        this.inverted = inverted;
        rebuildMesh(StartX, StartY, StartZ);
    }
    
    public void swtichSkin(boolean s){
        this.skin = s;
        rebuildMesh(StartX, StartY, StartZ);
    }

    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
   
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }

    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }

    public Chunk(int startX, int startY, int startZ) {
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
       
        try{texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
            }
        catch(Exception e) {
            System.out.print("ER-ROAR!");
        }
        
       
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if(r.nextFloat()>0.8f){
                        
                            Blocks[x][y][z] = new
                            Block(Block.BlockType.BlockType_Grass);
                        
                    }else if(r.nextFloat()>0.6f){
                        
                            Blocks[x][y][z] = new
                            Block(Block.BlockType.BlockType_Dirt);
                        
                        
                    }else if(r.nextFloat()>0.4f){
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Water);
                    }else if(r.nextFloat()>0.2f){
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Stone);
                    }else if(r.nextFloat()>0.1f){
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Bedrock);
                    }else if(r.nextFloat()>0.0f){
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Sand);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
   
     public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch (block.GetID()) {
            case 0:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
               
            case 1:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP!
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // FRONT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // BACK QUAD
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // LEFT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2,
                // RIGHT QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*3, y + offset*2};
               
            case 2:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*15, y + offset*13,
                x + offset*14, y + offset*13,
                x + offset*15, y + offset*12,
                x + offset*14, y + offset*12,
                // TOP!
                x + offset*15, y + offset*13,
                x + offset*14, y + offset*13,
                x + offset*14, y + offset*12,
                x + offset*15, y + offset*12,
                // FRONT QUAD
                x + offset*14, y + offset*12,
                x + offset*15, y + offset*12,
                x + offset*15, y + offset*13,
                x + offset*14, y + offset*13,
                // BACK QUAD
                x + offset*15, y + offset*13,
                x + offset*14, y + offset*13,
                x + offset*14, y + offset*12,
                x + offset*15, y + offset*12,
                // LEFT QUAD
                x + offset*14, y + offset*12,
                x + offset*15, y + offset*12,
                x + offset*15, y + offset*13,
                x + offset*15, y + offset*13,
                // RIGHT QUAD
                x + offset*14, y + offset*12,
                x + offset*15, y + offset*12,
                x + offset*15, y + offset*13,
                x + offset*14, y + offset*13};
               
            case 3:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
               
            case 4:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // TOP!
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // FRONT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                // LEFT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1,
                // RIGHT QUAD
                x + offset*1, y + offset*0,
                x + offset*2, y + offset*0,
                x + offset*2, y + offset*1,
                x + offset*1, y + offset*1};
               
            case 5:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // TOP!
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2};
               
            case 6:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                // TOP!
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                // FRONT QUAD
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                // BACK QUAD
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                // LEFT QUAD
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                // RIGHT QUAD
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7};
                
            case 7:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                // TOP!
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                // FRONT QUAD
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                // BACK QUAD
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                // LEFT QUAD
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                // RIGHT QUAD
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15};
            
            case 8:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3,
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                // TOP!
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3,
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                // FRONT QUAD
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3,
                // BACK QUAD
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3,
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                // LEFT QUAD
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3,
                // RIGHT QUAD
                x + offset*5, y + offset*2,
                x + offset*6, y + offset*2,
                x + offset*6, y + offset*3,
                x + offset*5, y + offset*3};
            case 9:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2,
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                // TOP!
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2,
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                // FRONT QUAD
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2,
                // BACK QUAD
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2,
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                // LEFT QUAD
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2,
                // RIGHT QUAD
                x + offset*7, y + offset*1,
                x + offset*8, y + offset*1,
                x + offset*8, y + offset*2,
                x + offset*7, y + offset*2};
                
            default:
                return new float[] {
                // Default texture coordinates
                   
                };
        }
    }
}