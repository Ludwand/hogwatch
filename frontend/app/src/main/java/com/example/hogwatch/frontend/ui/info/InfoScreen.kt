package com.example.hogwatch.frontend.ui.info


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hogwatch.frontend.R
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
   onNavigateBack: () -> Unit
) {
   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text("Hedgehog Info") },
            navigationIcon = {
               IconButton(onClick = onNavigateBack) {
                  Icon(
                     imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = "Back"
                  )
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Image(
            painter = painterResource(id = R.drawable.hedgehog_pic),
            contentDescription = "Picture of Hedgehog",
            modifier = Modifier
               .fillMaxWidth()
               .height(240.dp),
            contentScale = ContentScale.Crop
         )

         Spacer(modifier = Modifier.height(8.dp))

         Text(
            text = "Erinaceus europaeus",
            fontStyle = FontStyle.Italic,
            fontSize = 20.sp
         )

         Spacer(modifier = Modifier.height(16.dp))
         Text(
            text = "Biology",
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp
         )
         Spacer(modifier = Modifier.height(4.dp))
         Text(
            text = "Size: 20-30cm.\n\nWeight: 450-1500g.\n\nLifespan: 2-5 years. Up to 10 in captivity.",
            modifier = Modifier.fillMaxWidth(),
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp
         )
         Spacer(modifier = Modifier.height(16.dp))
         Text(
            text = "Living Habits",
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp
         )
         Spacer(modifier = Modifier.height(4.dp))
         Text(
            text = "Commonly lives near human settlements, such as parks, gardens and hedges. Also favors leafy forests and piles of leaves. Avoids pine forests and open farmland.\n\n" +
                    "Hedgehogs are omnivores. Their diet consists of small vertebrates such as frogs, lizards, offspring of smaller animals, and intertebrates such as worms, snails and insects. They also eat fruit, vegetables and fungi. \n\n" +
                    "Lives in solitude until breeding season which starts in spring. The hedgehog has a gestation period of 5-6 weeks, and the hoglets stay in the nest for about 3 weeks and then follow their mother for a couple more weeks. \n\n" +
                    "The European hedgehog enters hibernation from October through to April. During this period, their body temperature goes as low as 1°C, and their heartbeat goes as low as 22 beats per minute. Common hibernation locations include outhouses, piles of leaves or sticks, garages, and composts. Winter mortality rate is 25-40%.",
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp
         )
         Spacer(modifier = Modifier.height(16.dp))
         Text(
            text = "Threats",
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp
         )
         Spacer(modifier = Modifier.height(4.dp))
         Text(
            text = "Natural predators of the hedgehogs in Sweden include: foxes, badgers and eagle-owls. Humans indirectly endanger hedgehogs mainly through increased car traffic. Furthermore, environmental " +
                    "poisons such as PCB and DDT gets stored in the fat deposits of the hedgehogs, and causes long-term exposition during hibernation.",
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp
         )
         Spacer(modifier = Modifier.height(16.dp))
         Text(
            text = "How We Can Help",
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp
         )
         Spacer(modifier = Modifier.height(4.dp))
         Text(
            text = "Provide the hedgehog with protective areas. Create a corner a corner in your garden with plenty of leaves, twigs and branches which the hedgehog can use for protection.\n\n" +
                    "Create a garden with lots of active life, providing the hedgehog with options of food such as insects, snails and worms. You can offer hedgehogs a bowl of water, but not milk as hedgehogs can't digest lactose. For food, you can provide it with cat or dog food soaked in water, " +
                    "or any protein-rich foods such as cooked ham.\n\n" +
                    "The hedgehog sleeps hidden away during the day. Check your hedges, composts and leaf piles before starting your gardening work to avoid disturbing them. If you own a pond or pool, provide a way for hedgehogs to exit them if they were to fall in. \n\n" +
                    "If you have got a fence between you and your neighbours, provide a small hole for it to walk through rather than trying to traverse by using a nearby road where it might get hurt.",
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp
         )

      }
   }
}