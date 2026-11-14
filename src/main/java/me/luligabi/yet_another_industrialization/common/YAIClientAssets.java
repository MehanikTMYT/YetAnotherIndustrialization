package me.luligabi.yet_another_industrialization.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** Client assets in the 26.1 blockstate and item codec formats. */
public final class YAIClientAssets implements DataProvider {
    private final Path root;
    private final Path outputRoot;

    private YAIClientAssets(PackOutput output) {
        outputRoot = output.getOutputFolder();
        root = outputRoot.resolve("assets").resolve(YAIIds.ID);
    }

    public static void gather(GatherDataEvent.Client event) {
        event.getGenerator().addProvider(true, new YAIClientAssets(event.getGenerator().getPackOutput()));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        try (var reader = new InputStreamReader(Objects.requireNonNull(
                YAIClientAssets.class.getResourceAsStream("/data_templates/yai_client_assets.json")), StandardCharsets.UTF_8)) {
            JsonObject assets = JsonParser.parseReader(reader).getAsJsonObject();
            var writes = new ArrayList<CompletableFuture<?>>();
            // Keep existing recipe, language, tag and shape outputs until their providers are ported.
            // DataGenerator otherwise removes every file not claimed by a provider.
            try (var paths = java.nio.file.Files.walk(outputRoot)) {
                for (var path : paths.filter(java.nio.file.Files::isRegularFile).filter(p -> !p.startsWith(outputRoot.resolve(".cache"))).toList()) {
                    if (path.startsWith(root) && assets.has(root.relativize(path).toString())) continue;
                    if (!path.toString().endsWith(".json")) {
                        byte[] bytes = java.nio.file.Files.readAllBytes(path);
                        output.writeIfNeeded(path, bytes, com.google.common.hash.Hashing.sha1().hashBytes(bytes));
                        continue;
                    }
                    try (var existing = java.nio.file.Files.newBufferedReader(path)) {
                        writes.add(DataProvider.saveStable(output, JsonParser.parseReader(existing), path));
                    }
                }
            }
            for (var item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
                var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
                if (id.getNamespace().equals(YAIIds.ID) && item instanceof net.minecraft.world.item.BlockItem
                        && !item.getDescriptionId().startsWith("block." + YAIIds.ID + ".")) {
                    throw new IllegalStateException("Block item has incorrect description prefix: " + id);
                }
                if (id.getNamespace().equals(YAIIds.ID) && !assets.has("items/" + id.getPath() + ".json")) {
                    throw new IllegalStateException("Missing client item definition for " + id);
                }
            }
            for (var entry : assets.entrySet()) {
                writes.add(DataProvider.saveStable(output, entry.getValue(), root.resolve(entry.getKey())));
            }
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public String getName() {
        return "YAI client assets";
    }
}
