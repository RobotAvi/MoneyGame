package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.R
import com.financialsuccess.game.databinding.ItemAssetBinding
import com.financialsuccess.game.models.Asset
import java.text.NumberFormat
import java.util.*

class AssetAdapter(
    private val assets: List<Asset>,
    private val onAssetClick: (Int) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    inner class AssetViewHolder(private val binding: ItemAssetBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(asset: Asset, position: Int) {
            binding.tvAssetName.text = asset.name
            binding.tvAssetType.text = getAssetTypeText(asset.type)
            binding.tvAssetValue.text = "Стоимость: ${currencyFormat.format(asset.value)}"
            binding.tvCashFlow.text = "Поток: +${currencyFormat.format(asset.cashFlow)}/мес"
            
            // Устанавливаем иконку актива
            val iconRes = when {
                asset.name.contains("ПИФ", true) -> R.drawable.investment_pif
                asset.name.contains("Яндекс", true) -> R.drawable.investment_yandex
                asset.name.contains("Стартап", true) -> R.drawable.investment_startup
                asset.name.contains("Ethereum", true) || asset.name.contains("ETH", true) -> R.drawable.investment_ethereum
                asset.name.contains("REIT", true) -> R.drawable.investment_reit
                asset.type == com.financialsuccess.game.models.AssetType.REAL_ESTATE -> R.drawable.asset_real_estate
                asset.type == com.financialsuccess.game.models.AssetType.STOCKS -> R.drawable.asset_stocks
                asset.type == com.financialsuccess.game.models.AssetType.BUSINESS -> R.drawable.asset_business
                asset.type == com.financialsuccess.game.models.AssetType.CRYPTO -> R.drawable.asset_crypto
                asset.type == com.financialsuccess.game.models.AssetType.BONDS -> R.drawable.asset_bonds
                else -> R.drawable.asset_real_estate
            }
            
            try {
                val iconField = binding.javaClass.getDeclaredField("ivAssetIcon")
                val imageView = iconField.get(binding) as? android.widget.ImageView
                imageView?.setBackgroundResource(iconRes)
            } catch (e: Exception) {
                // Игнорируем если поле не найдено (совместимость)
            }
            
            // Расчет и показ ROI
            val roi = if (asset.downPayment > 0) {
                (asset.cashFlow * 12.0 / asset.downPayment) * 100
            } else 0.0
            
            // Проверяем, есть ли поле ROI в layout (добавлено в новой версии)
            try {
                val roiField = binding.javaClass.getDeclaredField("tvRoi")
                val tvRoi = roiField.get(binding) as? android.widget.TextView
                tvRoi?.text = "ROI: ${String.format("%.1f", roi)}%"
            } catch (e: Exception) {
                // Игнорируем если поле не найдено (совместимость со старой версией)
            }
            
            if (asset.shares > 1) {
                binding.tvShares.text = "Количество: ${asset.shares}"
            } else {
                binding.tvShares.text = ""
            }
            
            binding.root.setOnClickListener {
                onAssetClick(position)
            }
        }
        
        private fun getAssetTypeText(type: com.financialsuccess.game.models.AssetType): String {
            return when (type) {
                com.financialsuccess.game.models.AssetType.REAL_ESTATE -> "Недвижимость"
                com.financialsuccess.game.models.AssetType.STOCKS -> "Акции"
                com.financialsuccess.game.models.AssetType.BUSINESS -> "Бизнес"
                com.financialsuccess.game.models.AssetType.CRYPTO -> "Криптовалюта"
                com.financialsuccess.game.models.AssetType.BONDS -> "Облигации"
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AssetViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(assets[position], position)
    }
    
    override fun getItemCount() = assets.size
}